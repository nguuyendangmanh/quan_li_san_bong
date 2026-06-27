package com.football.service;

import com.football.dto.CustomerDTO;
import com.football.entity.User;
import com.football.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    // Ngưỡng điểm VIP — chỉnh ở đây, không cần đụng chỗ khác
    private static final int NGUONG_BAC       = 100;
    private static final int NGUONG_VANG      = 300;
    private static final int NGUONG_KIMCUONG  = 600;

    // Điểm thưởng cho mỗi 100.000đ thanh toán
    private static final double TY_LE_DIEM = 1.0 / 100_000.0;

    @Autowired
    private UserRepository userRepository;

    // Lấy toàn bộ khách hàng kèm hạng VIP
    public List<CustomerDTO> getAllCustomers() {
        return userRepository.findAllCustomersOrderByPoints()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Lấy thông tin 1 khách
    public CustomerDTO getCustomerById(Integer id) {
        User user = userRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + id));
        return toDTO(user);
    }

    // Cộng điểm sau khi booking COMPLETED — gọi từ BookingService (TV3)
    @Transactional
    public void addPointsAfterBooking(Integer userId, double totalPrice) {
        int diemCong = (int) Math.floor(totalPrice * TY_LE_DIEM);
        if (diemCong > 0) {
            userRepository.addLoyaltyPoints(Long.valueOf(userId), diemCong);
        }
    }

    // Tính % giảm giá theo hạng VIP — dùng để BookingService tính total_price
    public double getVipDiscount(Integer userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng: " + userId));
        return tinhGiamGia(user.getLoyaltyPoints());
    }

    // --- helper ---

    private CustomerDTO toDTO(User u) {
        return new CustomerDTO(u.getId() != null ? u.getId().intValue() : null, u.getPhone(), u.getFullName(), u.getRole(), u.getLoyaltyPoints());
    }

    private double tinhGiamGia(Integer points) {
        if (points == null || points < NGUONG_BAC)       return 0.0;
        if (points < NGUONG_VANG)                         return 0.05; // 5%
        if (points < NGUONG_KIMCUONG)                     return 0.10; // 10%
        return 0.15;                                                    // 15%
    }

    public String getVipTierName(Integer points) {
        if (points == null || points < NGUONG_BAC)       return "DONG";
        if (points < NGUONG_VANG)                         return "BAC";
        if (points < NGUONG_KIMCUONG)                     return "VANG";
        return "KIMCUONG";
    }
}
