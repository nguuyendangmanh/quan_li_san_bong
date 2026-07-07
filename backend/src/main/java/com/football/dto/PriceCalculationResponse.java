package com.football.dto;

import java.util.List;

/**
 * DTO trả về kết quả tính giá cho Frontend sau khi chạy thuật toán giá đa hình.
 * Gồm: Tổng tiền phải trả + Chi tiết từng giờ để Frontend hiển thị bảng breakdown rõ ràng.
 */
public class PriceCalculationResponse {

    /** Tên sân bóng (để hiển thị trên UI) */
    private String fieldName;

    /** Giờ bắt đầu đặt sân */
    private Integer startHour;

    /** Giờ kết thúc đặt sân */
    private Integer endHour;

    /** Tổng số giờ đặt */
    private Integer totalHours;

    /** Tổng tiền phải trả (VND) - đã áp dụng hệ số giờ vàng */
    private Double totalPrice;

    /**
     * Chi tiết giá từng giờ.
     * Giúp Frontend hiển thị bảng breakdown để người dùng hiểu tại sao giá lại như vậy.
     * VD: "17:00 – 18:00: 200,000 × 1.5 = 300,000 VND (Giờ Vàng)"
     */
    private List<HourlyDetail> hourlyDetails;

    // ========== INNER CLASS: CHI TIẾT TỪNG GIỜ ==========
    /**
     * Lớp con đại diện cho chi tiết tính giá của 1 giờ cụ thể.
     * Giúp Frontend hiển thị bảng phân tích giá minh bạch.
     */
    public static class HourlyDetail {
        /** Giờ bắt đầu của đoạn 1 giờ này (VD: 17) */
        private Integer hour;

        /** Giá tiền của riêng giờ này (đã nhân multiplier nếu có) */
        private Double price;

        /** Hệ số nhân được áp dụng (1.0 nếu là giờ thường) */
        private Double multiplier;

        /** Nhãn giờ (VD: "Giờ Vàng" hoặc "Giờ Thường") */
        private String label;

        public HourlyDetail() {}

        public HourlyDetail(Integer hour, Double price, Double multiplier, String label) {
            this.hour = hour;
            this.price = price;
            this.multiplier = multiplier;
            this.label = label;
        }

        public Integer getHour() { return hour; }
        public void setHour(Integer hour) { this.hour = hour; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Double getMultiplier() { return multiplier; }
        public void setMultiplier(Double multiplier) { this.multiplier = multiplier; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
    }

    // ========== GETTERS & SETTERS ==========
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public Integer getStartHour() { return startHour; }
    public void setStartHour(Integer startHour) { this.startHour = startHour; }

    public Integer getEndHour() { return endHour; }
    public void setEndHour(Integer endHour) { this.endHour = endHour; }

    public Integer getTotalHours() { return totalHours; }
    public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public List<HourlyDetail> getHourlyDetails() { return hourlyDetails; }
    public void setHourlyDetails(List<HourlyDetail> hourlyDetails) { this.hourlyDetails = hourlyDetails; }
}
