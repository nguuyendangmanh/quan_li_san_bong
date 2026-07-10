package com.football.controller;

import com.football.dto.DashboardStatsDTO;
import com.football.dto.FieldRevenueDTO;
import com.football.dto.RevenueReportDTO;
import com.football.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // GET /api/reports/dashboard — Số liệu tổng quan cho 4 thẻ trên đầu trang Admin
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }

    // GET /api/reports/revenue/monthly?nam=2025 — Doanh thu 12 tháng trong năm (biểu đồ cột)
    @GetMapping("/revenue/monthly")
    public ResponseEntity<List<RevenueReportDTO>> getMonthlyRevenue(
            @RequestParam(required = false) Integer nam) {
        int namQuery = (nam != null) ? nam : Year.now().getValue();
        return ResponseEntity.ok(reportService.getDoanhThuTheoThang(namQuery));
    }

    // GET /api/reports/revenue/daily?nam=2025&thang=6 — Doanh thu từng ngày trong tháng (biểu đồ đường)
    @GetMapping("/revenue/daily")
    public ResponseEntity<List<RevenueReportDTO>> getDailyRevenue(
            @RequestParam int nam,
            @RequestParam int thang) {
        return ResponseEntity.ok(reportService.getDoanhThuTheoNgay(nam, thang));
    }

    // GET /api/reports/revenue/fields?nam=2025 — Doanh thu theo từng sân (biểu đồ tròn)
    @GetMapping("/revenue/fields")
    public ResponseEntity<List<FieldRevenueDTO>> getFieldRevenue(
            @RequestParam(required = false) Integer nam) {
        int namQuery = (nam != null) ? nam : Year.now().getValue();
        return ResponseEntity.ok(reportService.getDoanhThuTheoSan(namQuery));
    }
}
