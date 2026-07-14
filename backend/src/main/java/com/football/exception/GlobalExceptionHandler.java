package com.football.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ============================================================
 * GLOBAL EXCEPTION HANDLER (Xử lý ngoại lệ toàn cục)
 * ============================================================
 * Áp dụng Design Pattern: Chain of Responsibility
 * - Tất cả các RuntimeException chưa được bắt ở Controller
 *   sẽ tự động "nổi lên" và được xử lý TẬP TRUNG tại đây.
 * - Giúp chuẩn hóa format JSON lỗi trả về cho Frontend.
 * - Tránh lộ stack trace nhạy cảm ra ngoài môi trường production.
 *
 * Lưu ý: Các Controller hiện tại đã có try-catch riêng nên
 * @ControllerAdvice này chỉ bắt các exception CHƯA được xử lý,
 * hoạt động như một "lưới an toàn" cuối cùng của hệ thống.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý tất cả RuntimeException chưa được bắt ở Controller.
     * Ví dụ: IllegalArgumentException, IllegalStateException, NullPointerException...
     *
     * @param ex RuntimeException chưa được xử lý
     * @return ResponseEntity với HTTP 400 Bad Request và body JSON chuẩn
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Xử lý AccessDeniedException - Trường hợp người dùng đã xác thực
     * nhưng không đủ quyền để truy cập tài nguyên (Ví dụ: STAFF gọi API của ADMIN).
     * Spring Security ném exception này trước khi vào Controller.
     *
     * @param ex AccessDeniedException từ Spring Security
     * @return ResponseEntity với HTTP 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "Bạn không có quyền thực hiện thao tác này.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Lưới an toàn cuối cùng - Bắt tất cả các Exception không lường trước.
     * Trả về HTTP 500 Internal Server Error mà không lộ thông tin nhạy cảm.
     *
     * @param ex Exception bất kỳ chưa được xử lý
     * @return ResponseEntity với HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
