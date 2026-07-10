package com.football.controller;

import com.football.dto.*;
import com.football.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ==========================================
 * CONTROLLER: QUẢN LÝ SÂN BÓNG (FIELD API)
 * ==========================================
 * Xử lý các HTTP Request từ Frontend liên quan đến module Sân bóng.
 * Endpoint gốc: /api/fields
 *
 * Danh sách API:
 *   GET    /api/fields                    → Lấy danh sách tất cả sân
 *   GET    /api/fields/{id}               → Lấy chi tiết 1 sân + config giờ vàng
 *   POST   /api/fields                    → Tạo sân mới (ADMIN)
 *   PUT    /api/fields/{id}               → Cập nhật sân (ADMIN/STAFF)
 *   DELETE /api/fields/{id}               → Xóa sân (ADMIN)
 *   GET    /api/fields/{id}/price-configs → Lấy danh sách giờ vàng của sân
 *   POST   /api/fields/calculate-price    → Tính giá tự động theo thuật toán đa hình
 */
@RestController
@RequestMapping("/api/fields")
@CrossOrigin(origins = "*")
public class FieldController {

    @Autowired
    private FieldService fieldService;

    // ============================================================
    // PHẦN 1: CRUD SÂN BÓNG
    // ============================================================

    /**
     * API Lấy danh sách tất cả sân bóng
     * Method: GET /api/fields
     * Quyền: Tất cả (kể cả khách chưa đăng nhập – để xem trước khi đặt)
     * @return Danh sách FieldResponseDTO
     */
    @GetMapping
    public ResponseEntity<List<FieldResponseDTO>> getAllFields() {
        // Gọi service lấy danh sách và trả về HTTP 200 OK
        List<FieldResponseDTO> fields = fieldService.getAllFields();
        return ResponseEntity.ok(fields);
    }

    /**
     * API Lấy chi tiết 1 sân bóng theo ID
     * Method: GET /api/fields/{id}
     * Quyền: Tất cả
     * @param id ID của sân cần xem
     * @return FieldResponseDTO kèm danh sách cấu hình giờ vàng
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFieldById(@PathVariable Long id) {
        try {
            // Tìm sân theo ID, ném lỗi nếu không tồn tại
            FieldResponseDTO field = fieldService.getFieldById(id);
            return ResponseEntity.ok(field);
        } catch (RuntimeException e) {
            // Không tìm thấy → trả về 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API Tạo sân bóng mới
     * Method: POST /api/fields
     * Quyền: ADMIN only (phân quyền sẽ cấu hình trong SecurityConfig)
     * @param request Thông tin sân mới (name, type, basePricePerHour, priceConfigs...)
     * @return FieldResponseDTO của sân vừa tạo, kèm HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<?> createField(@RequestBody FieldRequestDTO request) {
        try {
            // Gọi service tạo sân mới (service sẽ validate và lưu DB)
            FieldResponseDTO createdField = fieldService.createField(request);
            // Trả về HTTP 201 Created (đúng convention REST khi tạo mới thành công)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdField);
        } catch (RuntimeException e) {
            // Lỗi nghiệp vụ (VD: trùng tên sân) → trả về 400 Bad Request
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API Cập nhật thông tin sân bóng
     * Method: PUT /api/fields/{id}
     * Quyền: ADMIN hoặc STAFF
     * @param id      ID của sân cần cập nhật
     * @param request Thông tin mới cần cập nhật
     * @return FieldResponseDTO sau khi đã được cập nhật
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateField(@PathVariable Long id, @RequestBody FieldRequestDTO request) {
        try {
            // Gọi service cập nhật sân (bao gồm cả PriceConfig giờ vàng)
            FieldResponseDTO updatedField = fieldService.updateField(id, request);
            return ResponseEntity.ok(updatedField);
        } catch (RuntimeException e) {
            // Không tìm thấy sân hoặc lỗi khác → 400
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API Xóa sân bóng
     * Method: DELETE /api/fields/{id}
     * Quyền: ADMIN only
     * @param id ID của sân cần xóa
     * @return HTTP 204 No Content nếu xóa thành công
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id) {
        try {
            fieldService.deleteField(id);
            // HTTP 204 No Content: Xóa thành công, không có nội dung trả về (chuẩn REST)
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // Không tìm thấy sân để xóa → 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ============================================================
    // PHẦN 2: QUẢN LÝ GIỜ VÀNG & TÍNH GIÁ
    // ============================================================

    /**
     * API Lấy danh sách cấu hình giờ vàng của 1 sân
     * Method: GET /api/fields/{id}/price-configs
     * Quyền: ADMIN/STAFF (để xem cấu hình)
     * @param id ID của sân cần xem cấu hình giá
     * @return Danh sách PriceConfigDTO
     */
    @GetMapping("/{id}/price-configs")
    public ResponseEntity<?> getPriceConfigs(@PathVariable Long id) {
        try {
            List<PriceConfigDTO> configs = fieldService.getPriceConfigsByFieldId(id);
            return ResponseEntity.ok(configs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * ★ API TÍNH GIÁ ĐA HÌNH THEO GIỜ VÀNG ★
     * Method: POST /api/fields/calculate-price
     * Quyền: Tất cả (kể cả khách hàng cần preview giá trước khi đặt)
     *
     * Frontend gửi lên: { "fieldId": 1, "startHour": 16, "endHour": 19 }
     * Backend trả về:   Tổng tiền + bảng chi tiết từng giờ (xem PriceCalculationResponse)
     *
     * @param request Chứa fieldId, startHour, endHour
     * @return PriceCalculationResponse với tổng tiền và breakdown chi tiết từng giờ
     */
    @PostMapping("/calculate-price")
    public ResponseEntity<?> calculatePrice(@RequestBody PriceCalculationRequest request) {
        try {
            // Gọi thuật toán tính giá đa hình trong Service
            PriceCalculationResponse result = fieldService.calculatePrice(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // Lỗi validate đầu vào (VD: giờ kết thúc < giờ bắt đầu)
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
