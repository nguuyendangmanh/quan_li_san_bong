package com.football.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Thuật toán băm mật khẩu
     * - Sử dụng BCrypt để mã hóa mật khẩu một chiều (không thể dịch ngược).
     * - Khi khách hàng đăng ký, mật khẩu sẽ được băm bằng thuật toán này.
     * - @Bean giúp Spring tự động nhận diện và tiêm (inject) vào AuthService.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình luồng Bảo mật chính của hệ thống (Security Filter Chain)
     * Đây là "Trạm gác" kiểm duyệt mọi Request từ Frontend gửi xuống.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Cho phép cấu hình CORS (Chống lỗi Cross-Origin khi Frontend gọi API)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Tắt CSRF (Vì hệ thống dùng JWT Token thay cho Session truyền thống)
            .csrf(csrf -> csrf.disable())
            // Tắt kiểm tra X-Frame-Options để H2 Console (dùng iframe) hiển thị được
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // Không lưu trạng thái Session trên Server (Stateless), hoàn toàn phụ thuộc vào JWT Token
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Cấu hình quy tắc phân quyền:
            .authorizeHttpRequests(auth -> auth
                // TẠM THỜI MỞ CỬA CHO TV2 (fields), TV4 (services) VÀ TV5 (reports) ĐỂ DỄ TEST CODE (TRƯỚC KHI NỘP BÀI CÓ THỂ ĐÓNG LẠI)
                // Mở thêm /h2-console/** để xem Database khi dùng H2 (chỉ dùng khi test, ĐÓNG LẠI khi nộp bài)
                .requestMatchers("/api/auth/**", "/error", "/api/fields/**", "/api/services/**", "/api/reports/**", "/h2-console/**").permitAll()
                // Tất cả các đường link khác (Ví dụ: Đặt sân, Lịch) đều bị chặn, bắt buộc phải có Token
                .anyRequest().authenticated()
            )
            // Nhúng bộ lọc JWT (JwtAuthenticationFilter) vào trước bộ lọc mặc định của Spring Security
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * Chống lỗi CORS: Mở cửa cho Frontend (chạy trên localhost hoặc domain khác) 
     * có thể gọi sang Backend một cách an toàn mà không bị trình duyệt chặn.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép tất cả các domain gọi vào API (có thể thay "*" bằng domain thực tế khi đưa lên mạng)
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        // Cho phép tất cả các phương thức HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Chấp nhận các Header quan trọng như Authorization (chứa Token) và Content-Type (dữ liệu JSON)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng cấu hình CORS này cho toàn bộ các endpoint (/**) của hệ thống
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
