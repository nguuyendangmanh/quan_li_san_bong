package com.football.controller;

import com.football.dto.RegisterRequest;
import com.football.entity.User;
import com.football.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/staff")
    public ResponseEntity<List<User>> getStaffList() {
        return ResponseEntity.ok(userService.getStaffList());
    }

    @PostMapping("/staff")
    public ResponseEntity<?> createStaff(@RequestBody RegisterRequest request) {
        try {
            User staff = userService.createStaff(request);
            return ResponseEntity.ok(staff);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {
        try {
            userService.deleteStaff(id);
            return ResponseEntity.ok("{\"message\": \"Xóa nhân viên thành công\"}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
