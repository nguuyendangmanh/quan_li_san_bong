package com.football.repository;

import com.football.entity.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookingServiceRepository extends JpaRepository<BookingService, Integer> {

  // Doanh thu dịch vụ ghép theo tháng (để cộng vào báo cáo doanh thu sân)
  @Query(value = """
      SELECT
          DATE_FORMAT(b.start_time, '%Y-%m') AS thoiGian,
          COALESCE(SUM(bs.subtotal), 0)       AS doanhThuDichVu
      FROM booking_services bs
      JOIN bookings b ON b.id = bs.booking_id
      WHERE b.status = 'COMPLETED'
        AND YEAR(b.start_time) = :nam
      GROUP BY DATE_FORMAT(b.start_time, '%Y-%m')
      ORDER BY thoiGian
      """, nativeQuery = true)
  List<Object[]> doanhThuDichVuTheoThang(@Param("nam") int nam);

  // Doanh thu dịch vụ ghép theo ngày
  @Query(value = """
      SELECT
          DATE_FORMAT(b.start_time, '%Y-%m-%d') AS thoiGian,
          COALESCE(SUM(bs.subtotal), 0)          AS doanhThuDichVu
      FROM booking_services bs
      JOIN bookings b ON b.id = bs.booking_id
      WHERE b.status = 'COMPLETED'
        AND YEAR(b.start_time)  = :nam
        AND MONTH(b.start_time) = :thang
      GROUP BY DATE_FORMAT(b.start_time, '%Y-%m-%d')
      ORDER BY thoiGian
      """, nativeQuery = true)
  List<Object[]> doanhThuDichVuTheoNgay(@Param("nam") int nam, @Param("thang") int thang);

  // Tổng doanh thu dịch vụ hôm nay (dashboard)
  @Query(value = """
      SELECT COALESCE(SUM(bs.subtotal), 0)
      FROM booking_services bs
      JOIN bookings b ON b.id = bs.booking_id
      WHERE b.status = 'COMPLETED'
        AND DATE(b.start_time) = CURDATE()
      """, nativeQuery = true)
  BigDecimal tongDoanhThuDichVuHomNay();

  // Tổng doanh thu dịch vụ tháng này (dashboard)
  @Query(value = """
      SELECT COALESCE(SUM(bs.subtotal), 0)
      FROM booking_services bs
      JOIN bookings b ON b.id = bs.booking_id
      WHERE b.status = 'COMPLETED'
        AND YEAR(b.start_time)  = YEAR(CURDATE())
        AND MONTH(b.start_time) = MONTH(CURDATE())
      """, nativeQuery = true)
  BigDecimal tongDoanhThuDichVuThangNay();
}
