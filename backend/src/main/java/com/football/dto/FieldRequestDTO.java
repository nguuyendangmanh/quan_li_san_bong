package com.football.dto;

import java.util.List;

/**
 * DTO dùng để nhận dữ liệu từ Frontend khi tạo mới hoặc cập nhật sân bóng.
 * (Request Body của POST /api/fields và PUT /api/fields/{id})
 */
public class FieldRequestDTO {

    /** Tên sân bóng */
    private String name;

    /** Loại sân: "5", "7", hoặc "11" (số người thi đấu) */
    private String type;

    /** Giá thuê cơ bản mỗi giờ (VND). Giá thực sẽ được tính lại theo giờ vàng */
    private Double basePricePerHour;

    /** Trạng thái: "AVAILABLE" hoặc "MAINTENANCE" */
    private String status;

    /** Mô tả chi tiết về sân */
    private String description;

    private String imageUrl;
    private String address;

    /**
     * Danh sách cấu hình giờ vàng gửi kèm khi tạo/sửa sân.
     * Nếu null hoặc rỗng, sân sẽ không có giờ vàng (dùng giá gốc).
     */
    private List<PriceConfigDTO> priceConfigs;

    // ========== GETTERS & SETTERS ==========
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
