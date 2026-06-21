package com.football.controller;

import com.football.dto.OrderServiceRequest;
import com.football.dto.ServiceDTO;
import com.football.entity.Service;
import com.football.entity.User;
import com.football.repository.UserRepository;
import com.football.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cung cấp các API quản lý dịch vụ và kho hàng cho hệ thống.
 * Phân quyền dựa trên JWT Token và kiểm tra thông tin User trong Database.
 */
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy vai trò của tài khoản đang thực hiện Request từ Token gửi kèm.
     */
    private String getCurrentUserRole() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhone(phone)
                .map(User::getRole)
                .orElse("CUSTOMER");
    }

    /**
     * API Lấy danh sách toàn bộ dịch vụ/đồ uống
     * Method: GET /api/services
     * Quyền hạn: Tất cả người dùng đã đăng nhập (ADMIN, STAFF, CUSTOMER)
     */
    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        return ResponseEntity.ok(inventoryService.getAllServices());
    }

    /**
     * API Thêm dịch vụ/nước uống mới vào danh mục
     * Method: POST /api/services
     * Quyền hạn: Chỉ ADMIN mới được phép thêm
     */
    @PostMapping
    public ResponseEntity<?> createService(@RequestBody ServiceDTO dto) {
        if (!"ADMIN".equals(getCurrentUserRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Bạn không có quyền thực hiện hành động này (Yêu cầu ADMIN)!\"}");
        }
        try {
            Service created = inventoryService.createService(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API Cập nhật thông tin dịch vụ hoặc bổ sung số lượng kho
     * Method: PUT /api/services/{id}
     * Quyền hạn: Chỉ ADMIN mới được phép cập nhật danh mục dịch vụ/kho
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable Long id, @RequestBody ServiceDTO dto) {
        if (!"ADMIN".equals(getCurrentUserRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Bạn không có quyền thực hiện hành động này (Yêu cầu ADMIN)!\"}");
        }
        try {
            Service updated = inventoryService.updateService(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API Xóa dịch vụ/nước uống
     * Method: DELETE /api/services/{id}
     * Quyền hạn: Chỉ ADMIN mới được phép xóa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable Long id) {
        if (!"ADMIN".equals(getCurrentUserRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Bạn không có quyền thực hiện hành động này (Yêu cầu ADMIN)!\"}");
        }
        try {
            inventoryService.deleteService(id);
            return ResponseEntity.ok("{\"message\": \"Xóa dịch vụ thành công!\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API Gọi đồ uống tại quầy cho khách và tự động trừ kho
     * Method: POST /api/services/order
     * Quyền hạn: ADMIN và STAFF mới được thực hiện
     */
    @PostMapping("/order")
    public ResponseEntity<?> orderServiceForBooking(@RequestBody OrderServiceRequest request) {
        String role = getCurrentUserRole();
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\": \"Bạn không có quyền thực hiện hành động này (Yêu cầu ADMIN hoặc STAFF)!\"}");
        }
        try {
            inventoryService.orderServiceForBooking(request);
            return ResponseEntity.ok("{\"message\": \"Gọi thêm đồ uống thành công!\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
