package com.football.repository;

import com.football.entity.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository quản lý giao tiếp cơ sở dữ liệu cho Entity BookingService.
 */
@Repository
public interface BookingServiceRepository extends JpaRepository<BookingService, Long> {

    /**
     * Tìm kiếm toàn bộ danh sách dịch vụ đã đặt cho một lượt đặt sân cụ thể.
     */
    List<BookingService> findByBookingId(Long bookingId);
}
