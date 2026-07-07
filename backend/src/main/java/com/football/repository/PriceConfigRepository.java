package com.football.repository;

import com.football.entity.PriceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho Entity PriceConfig (cấu hình giá khung giờ vàng).
 * Cung cấp các truy vấn để lấy và xóa PriceConfig theo sân (Field).
 */
@Repository
public interface PriceConfigRepository extends JpaRepository<PriceConfig, Long> {

    /**
     * Lấy toàn bộ cấu hình giờ vàng của 1 sân cụ thể.
     * SELECT * FROM price_configs WHERE field_id = ?
     * Dùng trong thuật toán tính giá đa hình: lấy về rồi duyệt từng khung giờ.
     */
    List<PriceConfig> findByFieldId(Long fieldId);

    /**
     * Xóa toàn bộ cấu hình giờ vàng cũ của 1 sân trước khi lưu cấu hình mới.
     * Phương pháp: "Xóa hết rồi tạo lại" giúp tránh xung đột dữ liệu khi cập nhật.
     */
    void deleteByFieldId(Long fieldId);
}
