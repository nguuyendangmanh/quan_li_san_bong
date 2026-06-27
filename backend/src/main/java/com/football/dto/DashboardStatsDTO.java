package com.football.dto;

import java.math.BigDecimal;

// Tổng quan dashboard: số liệu nhanh hiển thị trên đầu trang admin
public class DashboardStatsDTO {

    private BigDecimal doanhThuHomNay;
    private BigDecimal doanhThuThangNay;
    private Long tongLuotDatHomNay;
    private Long tongKhachHang;
    private Long sanDangHoatDong;
    private Long tongLuotDatThangNay;

    public DashboardStatsDTO() {}

    public BigDecimal getDoanhThuHomNay() { return doanhThuHomNay; }
    public void setDoanhThuHomNay(BigDecimal doanhThuHomNay) { this.doanhThuHomNay = doanhThuHomNay; }

    public BigDecimal getDoanhThuThangNay() { return doanhThuThangNay; }
    public void setDoanhThuThangNay(BigDecimal doanhThuThangNay) { this.doanhThuThangNay = doanhThuThangNay; }

    public Long getTongLuotDatHomNay() { return tongLuotDatHomNay; }
    public void setTongLuotDatHomNay(Long tongLuotDatHomNay) { this.tongLuotDatHomNay = tongLuotDatHomNay; }

    public Long getTongKhachHang() { return tongKhachHang; }
    public void setTongKhachHang(Long tongKhachHang) { this.tongKhachHang = tongKhachHang; }

    public Long getSanDangHoatDong() { return sanDangHoatDong; }
    public void setSanDangHoatDong(Long sanDangHoatDong) { this.sanDangHoatDong = sanDangHoatDong; }

    public Long getTongLuotDatThangNay() { return tongLuotDatThangNay; }
    public void setTongLuotDatThangNay(Long tongLuotDatThangNay) { this.tongLuotDatThangNay = tongLuotDatThangNay; }
}
