package com.football.dto;

import java.math.BigDecimal;

// Doanh thu theo từng sân — dùng cho biểu đồ cột so sánh sân
public class FieldRevenueDTO {

    private String tenSan;
    private BigDecimal doanhThu;
    private Long soLuotDat;

    public FieldRevenueDTO() {}

    public FieldRevenueDTO(String tenSan, BigDecimal doanhThu, Long soLuotDat) {
        this.tenSan = tenSan;
        this.doanhThu = doanhThu != null ? doanhThu : BigDecimal.ZERO;
        this.soLuotDat = soLuotDat;
    }

    public String getTenSan() { return tenSan; }
    public void setTenSan(String tenSan) { this.tenSan = tenSan; }

    public BigDecimal getDoanhThu() { return doanhThu; }
    public void setDoanhThu(BigDecimal doanhThu) { this.doanhThu = doanhThu; }

    public Long getSoLuotDat() { return soLuotDat; }
    public void setSoLuotDat(Long soLuotDat) { this.soLuotDat = soLuotDat; }
}
