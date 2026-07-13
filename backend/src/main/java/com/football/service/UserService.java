package com.football.service;

import com.football.dto.RegisterRequest;
import com.football.entity.User;
import com.football.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getStaffList() {
        return userRepository.findByRole("STAFF");
    }

    public User createStaff(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        User staff = new User();
        staff.setFullName(request.getFullName());
        staff.setPhone(request.getPhone());
        staff.setEmail(request.getEmail());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setRole("STAFF");
        staff.setManagedFieldIds(request.getManagedFieldIds());

        return userRepository.save(staff);
    }

    public void deleteStaff(Integer id) {
        User staff = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
        if (!"STAFF".equals(staff.getRole())) {
            throw new RuntimeException("Không thể xóa user này (chỉ xóa STAFF)");
        }
        userRepository.delete(staff);
    }
}
