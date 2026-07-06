package com.football.dto;

/**
 * DTO đại diện cho 1 cấu hình giờ vàng (khung giờ đặc biệt).
 * Dùng trong cả chiều gửi lên (Request) và chiều nhận về (Response).
 */
public class PriceConfigDTO {

    /** ID của cấu hình (null nếu là tạo mới) */
    private Long id;

    /** Giờ bắt đầu áp dụng (0-23). VD: 17 = 17:00 */
    private Integer startHour;

    /** Giờ kết thúc áp dụng (1-24). VD: 21 = 21:00 */
    private Integer endHour;

    /**
     * Hệ số nhân giá.
     * Giá thực = basePricePerHour × multiplier
     * VD: 1.5 nghĩa là tăng 50% so với giá gốc
     */
    private Double multiplier;

    /** Nhãn hiển thị. VD: "Giờ Vàng", "Giờ Cao Điểm" */
    private String label;

    // ========== CONSTRUCTORS ==========
    public PriceConfigDTO() {}

    public PriceConfigDTO(Long id, Integer startHour, Integer endHour, Double multiplier, String label) {
        this.id = id;
        this.startHour = startHour;
        this.endHour = endHour;
        this.multiplier = multiplier;
        this.label = label;
    }

    // ========== GETTERS & SETTERS ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getStartHour() { return startHour; }
    public void setStartHour(Integer startHour) { this.startHour = startHour; }

    public Integer getEndHour() { return endHour; }
    public void setEndHour(Integer endHour) { this.endHour = endHour; }

    public Double getMultiplier() { return multiplier; }
    public void setMultiplier(Double multiplier) { this.multiplier = multiplier; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
