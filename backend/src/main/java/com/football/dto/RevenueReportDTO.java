package com.football.dto;

import java.math.BigDecimal;

// Doanh thu theo từng ngày hoặc từng tháng
public class RevenueReportDTO {

    private String thoiGian;        // "2025-06" hoặc "2025-06-15"
    private BigDecimal doanhThuSan; // Tổng tiền từ bookings.total_price
    private BigDecimal doanhThuDichVu; // Tổng tiền từ booking_services
    private BigDecimal tongDoanhThu;
    private Long soLuotDat;

    public RevenueReportDTO() {}

    public RevenueReportDTO(String thoiGian,
                            BigDecimal doanhThuSan,
                            BigDecimal doanhThuDichVu,
                            Long soLuotDat) {
        this.thoiGian = thoiGian;
        this.doanhThuSan = doanhThuSan != null ? doanhThuSan : BigDecimal.ZERO;
        this.doanhThuDichVu = doanhThuDichVu != null ? doanhThuDichVu : BigDecimal.ZERO;
        this.tongDoanhThu = this.doanhThuSan.add(this.doanhThuDichVu);
        this.soLuotDat = soLuotDat;
    }

    public String getThoiGian() { return thoiGian; }
    public void setThoiGian(String thoiGian) { this.thoiGian = thoiGian; }

    public BigDecimal getDoanhThuSan() { return doanhThuSan; }
    public void setDoanhThuSan(BigDecimal doanhThuSan) { this.doanhThuSan = doanhThuSan; }

    public BigDecimal getDoanhThuDichVu() { return doanhThuDichVu; }
    public void setDoanhThuDichVu(BigDecimal doanhThuDichVu) { this.doanhThuDichVu = doanhThuDichVu; }

    public BigDecimal getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(BigDecimal tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public Long getSoLuotDat() { return soLuotDat; }
    public void setSoLuotDat(Long soLuotDat) { this.soLuotDat = soLuotDat; }
}
