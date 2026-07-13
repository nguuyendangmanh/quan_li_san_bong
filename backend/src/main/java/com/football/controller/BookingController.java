package com.football.controller;

import com.football.dto.BookingRequestDTO;
import com.football.dto.BookingResponseDTO;
import com.football.service.FieldBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private FieldBookingService fieldBookingService;

    // Khách hàng đặt sân
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO request, Authentication auth) {
        try {
            BookingResponseDTO dto = fieldBookingService.createBooking(request, auth.getName());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Nhân viên/Admin lấy danh sách yêu cầu
    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings(Authentication auth) {
        return ResponseEntity.ok(fieldBookingService.getAllBookings(auth.getName()));
    }

    // Khách hàng xem lịch sử đặt sân của mình
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(Authentication auth) {
        return ResponseEntity.ok(fieldBookingService.getMyBookings(auth.getName()));
    }

    // Khách hàng thanh toán cọc
    @PostMapping("/{id}/pay-deposit")
    public ResponseEntity<?> payDeposit(@PathVariable Integer id, Authentication auth) {
        try {
            // Chỉ cập nhật trạng thái sang CONFIRMED. Trong thực tế sẽ cần call webhook ngân hàng.
            BookingResponseDTO dto = fieldBookingService.updateStatus(id, "CONFIRMED");
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Khách hàng tự hủy đơn
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelMyBooking(@PathVariable Integer id, Authentication auth) {
        try {
            BookingResponseDTO dto = fieldBookingService.cancelMyBooking(id, auth.getName());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Nhân viên/Admin cập nhật trạng thái (Duyệt/Từ chối/Check-in/Thanh toán)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            if (status == null) throw new RuntimeException("Trạng thái không được để trống.");
            BookingResponseDTO dto = fieldBookingService.updateStatus(id, status);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
