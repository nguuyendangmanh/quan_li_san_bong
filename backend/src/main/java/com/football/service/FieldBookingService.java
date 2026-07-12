package com.football.service;

import com.football.dto.*;
import com.football.entity.*;
import com.football.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FieldBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private BookingServiceRepository bookingServiceRepository;
    
    @Autowired
    private FieldService fieldService;
    
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CustomerService customerService;

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO dto, String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với số điện thoại này."));
                
        // Kiểm tra giờ hợp lệ (phải từ giờ chẵn, v.d. 15:00)
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new RuntimeException("Giờ bắt đầu phải trước giờ kết thúc.");
        }
        
        // Kiểm tra trùng lịch
        Long overlaps = bookingRepository.countOverlappingBookings(dto.getFieldId(), dto.getStartTime(), dto.getEndTime());
        if (overlaps > 0) {
            throw new RuntimeException("Sân đã có người đặt trong khung giờ này. Vui lòng chọn giờ khác.");
        }
        
        // Tính tiền sân
        PriceCalculationRequest priceReq = new PriceCalculationRequest();
        priceReq.setFieldId(Long.valueOf(dto.getFieldId()));
        priceReq.setStartHour(dto.getStartTime().getHour());
        priceReq.setEndHour(dto.getEndTime().getHour());
        PriceCalculationResponse priceRes = fieldService.calculatePrice(priceReq);
        
        // Lấy % giảm giá VIP
        double vipDiscount = customerService.getVipDiscount(user.getId().intValue());
        
        BigDecimal rawTotal = BigDecimal.valueOf(priceRes.getTotalPrice());
        BigDecimal discountAmount = rawTotal.multiply(BigDecimal.valueOf(vipDiscount));
        BigDecimal fieldTotal = rawTotal.subtract(discountAmount);
        BigDecimal deposit = fieldTotal.multiply(BigDecimal.valueOf(0.3)); // Cọc 30% sau khi giảm giá
        
        Booking booking = new Booking();
        booking.setUserId(user.getId().intValue());
        booking.setFieldId(dto.getFieldId());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setStatus("PENDING"); // Chờ duyệt
        booking.setDepositAmount(deposit);
        booking.setTotalPrice(fieldTotal);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Thêm nước uống nếu có
        if (dto.getServices() != null && !dto.getServices().isEmpty()) {
            OrderServiceRequest orderReq = new OrderServiceRequest();
            orderReq.setBookingId(Long.valueOf(savedBooking.getId()));
            
            List<OrderServiceRequest.Item> items = dto.getServices().stream().map(s -> {
                OrderServiceRequest.Item item = new OrderServiceRequest.Item();
                item.setServiceId(s.getServiceId());
                item.setQuantity(s.getQuantity());
                return item;
            }).collect(Collectors.toList());
            
            orderReq.setItems(items);
            inventoryService.orderServiceForBooking(orderReq);
            
            // Tính lại tổng tiền sau khi thêm nước (chỉ cộng tạm, chưa thanh toán)
            // Lẽ ra phần cập nhật totalPrice vào booking có thể làm luôn, nhưng sẽ được cộng khi thanh toán.
        }
        
        return toResponseDTO(savedBooking);
    }
    
    @Transactional
    public BookingResponseDTO updateStatus(Integer id, String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt sân."));
        
        // Nếu chuyển từ trạng thái khác sang COMPLETED, thì cộng điểm tích luỹ
        if ("COMPLETED".equals(status) && !"COMPLETED".equals(booking.getStatus())) {
            double finalTotal = booking.getTotalPrice().doubleValue();
            
            // Cộng thêm tiền nước vào tổng để tính điểm
            List<BookingService> bsList = bookingServiceRepository.findByBookingId(Long.valueOf(booking.getId()));
            for (BookingService bs : bsList) {
                finalTotal += bs.getSubtotal();
            }
            
            customerService.addPointsAfterBooking(booking.getUserId(), finalTotal);
        }
        
        booking.setStatus(status);
        booking.setUpdatedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);
        return toResponseDTO(booking);
    }
    
    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<BookingResponseDTO> getMyBookings(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user."));
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId().intValue()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    private BookingResponseDTO toResponseDTO(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUserId());
        dto.setFieldId(booking.getFieldId());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setDepositAmount(booking.getDepositAmount());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setCreatedAt(booking.getCreatedAt());
        
        userRepository.findById(Long.valueOf(booking.getUserId())).ifPresent(u -> {
            dto.setCustomerName(u.getFullName());
            dto.setCustomerPhone(u.getPhone());
            dto.setCustomerEmail(u.getEmail());
            dto.setCustomerVipTier(customerService.getVipTierName(u.getLoyaltyPoints()));
        });
        
        fieldRepository.findById(Long.valueOf(booking.getFieldId())).ifPresent(f -> {
            dto.setFieldName(f.getName());
        });

        List<BookingServiceDTO> serviceDTOs = bookingServiceRepository.findByBookingId(Long.valueOf(booking.getId()))
                .stream().map(bs -> {
                    BookingServiceDTO s = new BookingServiceDTO();
                    s.setServiceName(bs.getService().getName());
                    s.setQuantity(bs.getQuantity());
                    s.setPrice(bs.getPrice());
                    s.setSubtotal(bs.getSubtotal());
                    return s;
                }).collect(Collectors.toList());
        dto.setServices(serviceDTOs);
        
        return dto;
    }

    public BookingResponseDTO cancelMyBooking(Integer bookingId, String username) {
        User user = userRepository.findByPhone(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user."));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch đặt."));

        if (booking.getUserId().intValue() != user.getId().intValue()) {
            throw new RuntimeException("Bạn không có quyền hủy lịch đặt này.");
        }

        if (!"PENDING".equals(booking.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy lịch đặt ở trạng thái chờ cọc.");
        }

        booking.setStatus("CANCELLED");
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        return toResponseDTO(booking);
    }
}
