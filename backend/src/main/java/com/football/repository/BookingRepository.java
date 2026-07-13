package com.football.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.football.entity.Booking;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    // ── Báo cáo theo THÁNG ─────────────────────────────────────────────────
    // Trả về [thoiGian, doanhThuSan, soLuotDat] — ghép doanhThuDichVu ở service
    @Query(value = """
        SELECT
            DATE_FORMAT(b.start_time, '%Y-%m')   AS thoiGian,
            COALESCE(SUM(b.total_price), 0)       AS doanhThuSan,
            COUNT(b.id)                           AS soLuotDat
        FROM bookings b
        WHERE b.status = 'COMPLETED'
          AND YEAR(b.start_time)  = :nam
        GROUP BY DATE_FORMAT(b.start_time, '%Y-%m')
        ORDER BY thoiGian
        """, nativeQuery = true)
    List<Object[]> doanhThuTheoThang(@Param("nam") int nam);

    // ── Báo cáo theo NGÀY trong 1 tháng ────────────────────────────────────
    @Query(value = """
        SELECT
            DATE_FORMAT(b.start_time, '%Y-%m-%d') AS thoiGian,
            COALESCE(SUM(b.total_price), 0)        AS doanhThuSan,
            COUNT(b.id)                            AS soLuotDat
        FROM bookings b
        WHERE b.status = 'COMPLETED'
          AND YEAR(b.start_time)  = :nam
          AND MONTH(b.start_time) = :thang
        GROUP BY DATE_FORMAT(b.start_time, '%Y-%m-%d')
        ORDER BY thoiGian
        """, nativeQuery = true)
    List<Object[]> doanhThuTheoNgay(@Param("nam") int nam, @Param("thang") int thang);

    // ── Doanh thu theo từng SÂN (cả năm) ────────────────────────────────────
    @Query(value = """
        SELECT
            f.name                          AS tenSan,
            COALESCE(SUM(b.total_price), 0) AS doanhThu,
            COUNT(b.id)                     AS soLuotDat
        FROM bookings b
        JOIN football_fields f ON f.id = b.field_id
        WHERE b.status = 'COMPLETED'
          AND YEAR(b.start_time) = :nam
        GROUP BY f.id, f.name
        ORDER BY doanhThu DESC
        """, nativeQuery = true)
    List<Object[]> doanhThuTheoSan(@Param("nam") int nam);

    // ── Tổng doanh thu hôm nay (cho dashboard) ──────────────────────────────
    @Query(value = """
        SELECT COALESCE(SUM(total_price), 0)
        FROM bookings
        WHERE status = 'COMPLETED'
          AND DATE(start_time) = CURDATE()
        """, nativeQuery = true)
    BigDecimal tongDoanhThuHomNay();

    // ── Tổng doanh thu tháng này ─────────────────────────────────────────────
    @Query(value = """
        SELECT COALESCE(SUM(total_price), 0)
        FROM bookings
        WHERE status = 'COMPLETED'
          AND YEAR(start_time)  = YEAR(CURDATE())
          AND MONTH(start_time) = MONTH(CURDATE())
        """, nativeQuery = true)
    BigDecimal tongDoanhThuThangNay();

    // ── Lượt đặt hôm nay ─────────────────────────────────────────────────────
    @Query(value = "SELECT COUNT(*) FROM bookings WHERE DATE(start_time) = CURDATE()", nativeQuery = true)
    Long demLuotDatHomNay();

    // ── Lượt đặt tháng này ───────────────────────────────────────────────────
    @Query(value = """
        SELECT COUNT(*) FROM bookings
        WHERE YEAR(start_time)  = YEAR(CURDATE())
          AND MONTH(start_time) = MONTH(CURDATE())
        """, nativeQuery = true)
    Long demLuotDatThangNay();

    // ── Số sân đang ACTIVE ───────────────────────────────────────────────────
    @Query(value = "SELECT COUNT(*) FROM football_fields WHERE status = 'AVAILABLE'", nativeQuery = true)
    Long demSanDangHoatDong();

    // ── Kiểm tra trùng lịch ──────────────────────────────────────────────────
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.fieldId = :fieldId AND b.status NOT IN ('CANCELLED', 'REJECTED') AND (b.startTime < :endTime AND b.endTime > :startTime)")
    Long countOverlappingBookings(@Param("fieldId") Integer fieldId, @Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);

    // ── Lấy danh sách cho Nhân viên ──────────────────────────────────────────
    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findAllOrderByCreatedAtDesc();

    // ── Lấy danh sách cho Khách hàng ─────────────────────────────────────────
    List<Booking> findByUserIdOrderByCreatedAtDesc(Integer userId);

    // ── Lấy danh sách booking theo trạng thái và thời gian ───────────────────
    List<Booking> findByStatusAndCreatedAtBefore(String status, java.time.LocalDateTime time);

    // ── Lấy danh sách cho Nhân viên theo các sân quản lý ────────────────────
    List<Booking> findByFieldIdInOrderByCreatedAtDesc(List<Integer> fieldIds);
}

