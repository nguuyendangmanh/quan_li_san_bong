package com.football.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity đại diện cho bảng "fields" trong Database.
 * Mỗi đối tượng Field tương ứng với 1 sân bóng thực tế trong hệ thống.
 */
@Entity
@Table(name = "football_fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tên sân bóng (VD: "Sân A", "Sân VIP 1")
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Loại sân theo số người thi đấu: "5" / "7" / "11"
     */
    @Column(nullable = false, length = 10)
    private String type;

    /**
     * Giá thuê cơ bản mỗi giờ (đơn vị: VND).
     * Đây là giá GỐC, giá thực tế sẽ được tính toán lại bởi thuật toán giá đa hình
     * dựa trên các PriceConfig (khung giờ vàng) được cấu hình cho từng sân.
     */
    @Column(nullable = false, name = "base_price_per_hour")
    private Double basePricePerHour;

    /**
     * Trạng thái hiện tại của sân.
     * - "AVAILABLE"   : Sân đang hoạt động và có thể đặt
     * - "MAINTENANCE" : Sân đang bảo trì, không thể đặt
     */
    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE";

    /**
     * Mô tả chi tiết về sân (cơ sở vật chất, tiện nghi, ghi chú đặc biệt...)
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Đường dẫn hình ảnh minh họa cho sân bóng
     */
    @Column(length = 1000)
    private String imageUrl;

    /**
     * Địa chỉ cụ thể của sân bóng
     */
    @Column(length = 255)
    private String address;

    /**
     * Danh sách các cấu hình giá theo khung giờ (giờ vàng).
     * Quan hệ 1-Nhiều: 1 Field có nhiều PriceConfig.
     * mappedBy = "field" nghĩa là Field không phải bên nắm giữ khóa ngoại.
     * CascadeType.ALL: Khi xóa sân, tự động xóa luôn các PriceConfig đi kèm.
     */
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceConfig> priceConfigs;

    // ========== CONSTRUCTORS ==========
    public Field() {}

    // ========== GETTERS & SETTERS ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getBasePricePerHour() { return basePricePerHour; }
    public void setBasePricePerHour(Double basePricePerHour) { this.basePricePerHour = basePricePerHour; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public List<PriceConfig> getPriceConfigs() { return priceConfigs; }
    public void setPriceConfigs(List<PriceConfig> priceConfigs) { this.priceConfigs = priceConfigs; }
}
