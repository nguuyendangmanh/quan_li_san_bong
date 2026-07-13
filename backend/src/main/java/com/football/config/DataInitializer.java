package com.football.config;

import com.football.entity.User;
import com.football.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem Admin đã tồn tại chưa
        java.util.Optional<User> adminOpt = userRepository.findByPhone("0988888888");
        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
            admin.setRole("ADMIN");
            admin.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(admin);
            System.out.println("Đã cập nhật tài khoản 0988888888 thành ADMIN / 123456");
        } else {
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
