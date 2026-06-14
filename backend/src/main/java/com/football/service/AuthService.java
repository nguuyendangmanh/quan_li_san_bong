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

    public AuthResponse login(AuthRequest request) {
        Optional<User> userOpt = userRepository.findByPhone(request.getPhone());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Lấy pass ngầm định mã hóa so sánh với pass khách gõ
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = tokenProvider.generateToken(user.getPhone(), user.getRole());
                return new AuthResponse(token, user.getRole(), "Đăng nhập thành công!");
            }
        }
        throw new RuntimeException("Số điện thoại hoặc mật khẩu không chính xác!");
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setPhone(request.getPhone());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Băm mật khẩu
        newUser.setRole("CUSTOMER"); // Khách đăng ký auto là CUSTOMER

        userRepository.save(newUser);

        // Sinh luôn token cho họ vào web luôn
        String token = tokenProvider.generateToken(newUser.getPhone(), newUser.getRole());
        return new AuthResponse(token, newUser.getRole(), "Đăng ký thành công!");
    }
}
