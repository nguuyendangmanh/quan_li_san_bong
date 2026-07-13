package com.football.repository;

import com.football.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository quản lý giao tiếp cơ sở dữ liệu cho Entity Service.
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    /**
     * Thuật toán trừ tồn kho an toàn chống tranh chấp đồng thời (Atomic Update).
     * - Chỉ trừ kho khi số lượng hiện tại (stockQuantity) lớn hơn hoặc bằng số lượng yêu cầu (qty).
     * - Trả về số lượng dòng bị ảnh hưởng (1 nếu thành công, 0 nếu không đủ hàng).
     */
    @Modifying
    @Query("UPDATE Service s SET s.stockQuantity = s.stockQuantity - :qty WHERE s.id = :id AND s.stockQuantity >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") Integer qty);

    java.util.List<Service> findByFieldIdAndIsDeletedFalse(Long fieldId);
    
    java.util.List<Service> findByIsDeletedFalse();

    @Modifying
    void deleteByFieldIdIsNull();
}
