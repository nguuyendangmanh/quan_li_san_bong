package com.football.repository;

import com.football.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    boolean existsByPhone(String phone);

    // Lấy danh sách khách hàng sắp xếp theo điểm giảm dần (dùng cho trang Quản lý VIP)
    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' ORDER BY u.loyaltyPoints DESC")
    List<User> findAllCustomersOrderByPoints();

    // Cộng thêm điểm tích luỹ cho khách hàng sau khi hoàn thành booking
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.loyaltyPoints = COALESCE(u.loyaltyPoints, 0) + :diem WHERE u.id = :userId")
    void addLoyaltyPoints(@Param("userId") Long userId, @Param("diem") int diem);

    // Đếm số người dùng theo role (dùng cho dashboard stats)
    long countByRole(String role);
}
