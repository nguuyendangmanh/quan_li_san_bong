package com.football.controller;

import com.football.dto.AuthRequest;
import com.football.dto.AuthResponse;
import com.football.dto.RegisterRequest;
import com.football.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * API Đăng nhập
     * Method: POST /api/auth/login
     * @param request Chứa Số điện thoại và Mật khẩu (Dữ liệu từ Frontend gửi lên dạng JSON)
     * @return ResponseEntity chứa Token JWT (nếu thành công) hoặc báo lỗi 400 Bad Request (nếu thất bại)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Gọi tầng Service để xử lý nghiệp vụ Đăng nhập
            AuthResponse response = authService.login(request);
            // Trả về HTTP Status 200 OK cùng dữ liệu JSON chứa Token
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Bắt lỗi (ví dụ: Sai mật khẩu) và trả về HTTP Status 400 kèm câu thông báo lỗi
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    /**
     * API Đăng ký tài khoản mới
     * Method: POST /api/auth/register
     * @param request Chứa Họ tên, SĐT, Email và Mật khẩu
     * @return ResponseEntity chứa Token JWT (nếu đăng ký thành công) hoặc báo lỗi 400 Bad Request (nếu trùng SĐT)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Gọi tầng Service để xử lý nghiệp vụ Đăng ký
            AuthResponse response = authService.register(request);
            // Trả về HTTP Status 200 OK cùng Token để frontend lưu vào LocalStorage
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Bắt lỗi trùng số điện thoại và báo cho frontend hiển thị màu đỏ
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
