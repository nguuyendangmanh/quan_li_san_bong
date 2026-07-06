package com.football.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entity đại diện cho bảng "price_configs" trong Database.
 * Mỗi bản ghi là 1 cấu hình khung giờ đặc biệt (thường là "Giờ Vàng") của 1 sân bóng cụ thể.
 *
 * Ví dụ thực tế:
 *   - Sân A: 17:00 – 21:00 → nhân giá × 1.5 (tức là tăng 50%)
 *   - Sân B: 06:00 – 08:00 → nhân giá × 1.2 (tức là tăng 20%)
 */
@Entity
@Table(name = "price_configs")
public class PriceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Khóa ngoại liên kết về sân bóng (Field).
     * @JsonIgnore: Ngăn vòng lặp JSON vô hạn khi serialize (Field → PriceConfig → Field → ...)
     * Đây là quy tắc bắt buộc trong dự án (xem antigravityrules).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    @JsonIgnore
    private Field field;

    /**
     * Giờ bắt đầu áp dụng giá đặc biệt (0–23).
     * VD: 17 nghĩa là bắt đầu từ 17:00
     */
    @Column(nullable = false, name = "start_hour")
    private Integer startHour;

    /**
     * Giờ kết thúc áp dụng giá đặc biệt (0–24).
     * VD: 21 nghĩa là kết thúc lúc 21:00
     */
    @Column(nullable = false, name = "end_hour")
    private Integer endHour;

    /**
     * Hệ số nhân giá (multiplier).
     * Giá thực = basePricePerHour × multiplier
     * VD: multiplier = 1.5 → giá tăng 50% so với giá gốc
     */
    @Column(nullable = false)
    private Double multiplier;

    /**
     * Nhãn hiển thị cho khung giờ này.
     * VD: "Giờ Vàng", "Giờ Cao Điểm Sáng", "Cuối tuần"
     */
    @Column(length = 50)
    private String label;

    // ========== CONSTRUCTORS ==========
    public PriceConfig() {}

    // ========== GETTERS & SETTERS ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Field getField() { return field; }
    public void setField(Field field) { this.field = field; }

    public Integer getStartHour() { return startHour; }
    public void setStartHour(Integer startHour) { this.startHour = startHour; }

    public Integer getEndHour() { return endHour; }
    public void setEndHour(Integer endHour) { this.endHour = endHour; }

    public Double getMultiplier() { return multiplier; }
    public void setMultiplier(Double multiplier) { this.multiplier = multiplier; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
