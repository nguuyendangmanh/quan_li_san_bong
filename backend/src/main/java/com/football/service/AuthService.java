package com.football.service;

import com.football.config.JwtTokenProvider;
import com.football.dto.AuthRequest;
import com.football.dto.AuthResponse;
import com.football.dto.RegisterRequest;
import com.football.entity.User;
import com.football.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * Hàm xử lý logic Đăng nhập (Login)
     * - Tìm kiếm User trong Database theo số điện thoại
     * - So sánh mật khẩu bằng thuật toán mã hóa BCrypt
     * - Nếu đúng, tiến hành cấp phát JWT Token để duy trì phiên đăng nhập
     */
    public AuthResponse login(AuthRequest request) {
        // Truy vấn Database để lấy thông tin User dựa trên Số điện thoại
        Optional<User> userOpt = userRepository.findByPhone(request.getPhone());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // passwordEncoder.matches(): Tự động băm mật khẩu người dùng gõ vào và so sánh với mã băm trong DB
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Tạo chữ ký số JWT chứa SĐT và Quyền (Role) để xác thực các request sau này
                String token = tokenProvider.generateToken(user.getPhone(), user.getRole());
                return new AuthResponse(token, user.getRole(), "Đăng nhập thành công!");
            }
        }
        // Ném ngoại lệ (Exception) nếu sai SĐT hoặc Mật khẩu, Controller sẽ bắt và trả về lỗi cho Frontend
        throw new RuntimeException("Số điện thoại hoặc mật khẩu không chính xác!");
    }

    /**
     * Hàm xử lý logic Đăng ký (Register)
     * - Kiểm tra tính duy nhất của Số điện thoại (Chống trùng lặp)
     * - Khởi tạo tài khoản mới, băm mật khẩu bảo mật trước khi lưu vào DB
     * - Cấp phát ngay JWT Token để trải nghiệm người dùng liền mạch (không cần đăng nhập lại)
     */
    public AuthResponse register(RegisterRequest request) {
        // Kiểm tra xem số điện thoại đã tồn tại trong hệ thống chưa
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        // Tạo mới Object User để mapping xuống Database (JPA/Hibernate)
        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setPhone(request.getPhone());
        newUser.setEmail(request.getEmail());
        
        // Tuyệt đối không lưu mật khẩu gốc (Plain text). Phải mã hóa BCrypt bảo mật 1 chiều
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); 
        
        // Mặc định gán quyền 'CUSTOMER' cho người dùng tự đăng ký ngoài trang chủ
        newUser.setRole("CUSTOMER"); 

        // Gọi JPA Repository để thực thi lệnh INSERT INTO users...
        userRepository.save(newUser);

        // Sinh JWT Token để cho phép người dùng vào web luôn không phải đăng nhập lại
        String token = tokenProvider.generateToken(newUser.getPhone(), newUser.getRole());
        return new AuthResponse(token, newUser.getRole(), "Đăng ký thành công!");
    }
}
