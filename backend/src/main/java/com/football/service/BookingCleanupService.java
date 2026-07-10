package com.football.service;

import com.football.entity.Booking;
import com.football.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingCleanupService {

    @Autowired
    private BookingRepository bookingRepository;

    // Chạy mỗi phút (60000ms)
    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredPendingBookings() {
        // Tìm các booking trạng thái PENDING và được tạo trước 30 phút tính từ thời điểm hiện tại
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore("PENDING", cutoffTime);

        if (!expiredBookings.isEmpty()) {
            for (Booking booking : expiredBookings) {
                booking.setStatus("CANCELLED");
                booking.setUpdatedAt(LocalDateTime.now());
                System.out.println("Tự động hủy đơn đặt sân ID=" + booking.getId() + " do quá hạn 30 phút chưa thanh toán cọc.");
            }
            bookingRepository.saveAll(expiredBookings);
        }
    }
}
