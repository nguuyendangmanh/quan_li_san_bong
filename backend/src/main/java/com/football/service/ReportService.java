package com.football.service;

import com.football.dto.DashboardStatsDTO;
import com.football.dto.FieldRevenueDTO;
import com.football.dto.RevenueReportDTO;
import com.football.repository.BookingRepository;
import com.football.repository.BookingServiceRepository;
import com.football.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingServiceRepository bookingServiceRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Báo cáo doanh thu theo THÁNG trong 1 năm ───────────────────────────
    public List<RevenueReportDTO> getDoanhThuTheoThang(int nam) {
        List<Object[]> sanRows     = bookingRepository.doanhThuTheoThang(nam);
        List<Object[]> dichVuRows  = bookingServiceRepository.doanhThuDichVuTheoThang(nam);

        // Ghép doanh thu dịch vụ vào map theo thoiGian
        Map<String, BigDecimal> dichVuMap = toMap(dichVuRows);

        List<RevenueReportDTO> result = new ArrayList<>();
        for (Object[] row : sanRows) {
            String thoiGian      = (String) row[0];
            BigDecimal doanhThuSan = toBigDecimal(row[1]);
            Long soLuotDat        = toLong(row[2]);
            BigDecimal doanhThuDV = dichVuMap.getOrDefault(thoiGian, BigDecimal.ZERO);
            result.add(new RevenueReportDTO(thoiGian, doanhThuSan, doanhThuDV, soLuotDat));
        }
        return result;
    }

    // ── Báo cáo doanh thu theo NGÀY trong 1 tháng ──────────────────────────
    public List<RevenueReportDTO> getDoanhThuTheoNgay(int nam, int thang) {
        List<Object[]> sanRows    = bookingRepository.doanhThuTheoNgay(nam, thang);
        List<Object[]> dichVuRows = bookingServiceRepository.doanhThuDichVuTheoNgay(nam, thang);

        Map<String, BigDecimal> dichVuMap = toMap(dichVuRows);

        List<RevenueReportDTO> result = new ArrayList<>();
        for (Object[] row : sanRows) {
            String thoiGian      = (String) row[0];
            BigDecimal doanhThuSan = toBigDecimal(row[1]);
            Long soLuotDat        = toLong(row[2]);
            BigDecimal doanhThuDV = dichVuMap.getOrDefault(thoiGian, BigDecimal.ZERO);
            result.add(new RevenueReportDTO(thoiGian, doanhThuSan, doanhThuDV, soLuotDat));
        }
        return result;
    }

    // ── Doanh thu theo từng SÂN (cả năm) ────────────────────────────────────
    public List<FieldRevenueDTO> getDoanhThuTheoSan(int nam) {
        List<Object[]> rows = bookingRepository.doanhThuTheoSan(nam);
        List<FieldRevenueDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            String tenSan       = (String) row[0];
            BigDecimal doanhThu = toBigDecimal(row[1]);
            Long soLuotDat      = toLong(row[2]);
            result.add(new FieldRevenueDTO(tenSan, doanhThu, soLuotDat));
        }
        return result;
    }

    // ── Số liệu nhanh cho DASHBOARD ─────────────────────────────────────────
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        BigDecimal doanhThuSanHom  = bookingRepository.tongDoanhThuHomNay();
        BigDecimal doanhThuDVHom   = bookingServiceRepository.tongDoanhThuDichVuHomNay();
        BigDecimal doanhThuHomNay  = safe(doanhThuSanHom).add(safe(doanhThuDVHom));

        BigDecimal doanhThuThangNay = safe(bookingRepository.tongDoanhThuThangNay())
                                    .add(safe(bookingServiceRepository.tongDoanhThuDichVuThangNay()));

        stats.setDoanhThuHomNay(doanhThuHomNay);
        stats.setDoanhThuThangNay(doanhThuThangNay);
        stats.setTongLuotDatHomNay(bookingRepository.demLuotDatHomNay());
        stats.setTongLuotDatThangNay(bookingRepository.demLuotDatThangNay());
        stats.setTongKhachHang(userRepository.countByRole("CUSTOMER"));
        stats.setSanDangHoatDong(bookingRepository.demSanDangHoatDong());

        return stats;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, BigDecimal> toMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], toBigDecimal(row[1]));
        }
        return map;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        return new BigDecimal(val.toString());
    }

    private Long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Long) return (Long) val;
        return ((Number) val).longValue();
    }

    private BigDecimal safe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }
}
