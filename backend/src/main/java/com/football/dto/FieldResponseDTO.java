package com.football.dto;

import java.util.List;

/**
 * DTO trả về cho Frontend khi lấy thông tin sân bóng.
 * Kết hợp thông tin của Field + danh sách PriceConfig (giờ vàng đã được cấu hình).
 * Tuân thủ nguyên tắc: KHÔNG bao giờ trả Entity thẳng ra ngoài.
 */
public class FieldResponseDTO {

    private Long id;
    private String name;
    private String type;
    private Double basePricePerHour;
    private String status;
    private String description;
    private String imageUrl;
    private String address;

    /**
     * Danh sách cấu hình giờ vàng của sân này.
     * Frontend sẽ dùng danh sách này để hiển thị bảng giá và preview tính tiền.
     */
    private List<PriceConfigDTO> priceConfigs;

    // ========== CONSTRUCTOR ==========
    public FieldResponseDTO() {}

    public FieldResponseDTO(Long id, String name, String type, Double basePricePerHour,
                            String status, String description, String imageUrl, String address, List<PriceConfigDTO> priceConfigs) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.basePricePerHour = basePricePerHour;
        this.status = status;
        this.description = description;
        this.imageUrl = imageUrl;
        this.address = address;
        this.priceConfigs = priceConfigs;
    }

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

    public List<PriceConfigDTO> getPriceConfigs() { return priceConfigs; }
    public void setPriceConfigs(List<PriceConfigDTO> priceConfigs) { this.priceConfigs = priceConfigs; }
}
