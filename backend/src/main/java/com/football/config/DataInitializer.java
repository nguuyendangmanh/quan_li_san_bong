package com.football.config;

import com.football.entity.User;
import com.football.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem Admin đã tồn tại chưa
        if (!userRepository.existsByPhone("0988888888")) {
            User admin = new User();
            admin.setPhone("0988888888");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setFullName("Nguyễn Văn Admin");
            admin.setEmail("admin@football.vn");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("Đã khởi tạo tài khoản ADMIN mặc định: 0988888888 / 123456");
        }
    }
}
