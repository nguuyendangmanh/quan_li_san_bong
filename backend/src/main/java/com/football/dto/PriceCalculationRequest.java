package com.football.dto;

/**
 * DTO nhận request tính giá từ Frontend.
 * Frontend gửi lên: ID sân + giờ bắt đầu + giờ kết thúc muốn đặt.
 * Backend sẽ chạy thuật toán giá đa hình và trả về tổng tiền.
 */
public class PriceCalculationRequest {

    /** ID của sân bóng muốn tính giá */
    private Long fieldId;

    /**
     * Giờ bắt đầu đặt sân (0–23).
     * VD: 15 nghĩa là bắt đầu từ 15:00
     */
    private Integer startHour;

    /**
     * Giờ kết thúc đặt sân (1–24).
     * VD: 18 nghĩa là kết thúc lúc 18:00 → tổng 3 giờ
     */
    private Integer endHour;

    // ========== GETTERS & SETTERS ==========
    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public Integer getStartHour() { return startHour; }
    public void setStartHour(Integer startHour) { this.startHour = startHour; }

    public Integer getEndHour() { return endHour; }
    public void setEndHour(Integer endHour) { this.endHour = endHour; }
}
