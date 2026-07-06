package com.football.repository;

import com.football.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho Entity Field.
 * JpaRepository cung cấp sẵn các hàm CRUD cơ bản:
 * save(), findById(), findAll(), deleteById()...
 * Chúng ta chỉ cần khai báo thêm các query tùy chỉnh bên dưới.
 */
@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    /**
     * Tìm danh sách sân theo loại (5 người / 7 người / 11 người).
     * Spring Data JPA tự động dịch tên hàm thành câu SQL:
     * SELECT * FROM fields WHERE type = ?
     */
    List<Field> findByType(String type);

    /**
     * Tìm danh sách sân theo trạng thái hiện tại.
     * SELECT * FROM fields WHERE status = ?
     * Dùng để lọc sân đang AVAILABLE khi khách hàng đặt sân.
     */
    List<Field> findByStatus(String status);

    /**
     * Kiểm tra xem tên sân đã tồn tại trong hệ thống chưa (tránh trùng tên).
     * SELECT COUNT(*) > 0 FROM fields WHERE name = ?
     */
    boolean existsByName(String name);
}
