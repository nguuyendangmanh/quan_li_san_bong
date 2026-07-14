package com.football.service;

import com.football.dto.AuthRequest;
import com.football.dto.AuthResponse;
import com.football.dto.RegisterRequest;
import com.football.entity.User;
import com.football.repository.UserRepository;
import com.football.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.football.service.CustomerService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setPhone("0987654321");
        mockUser.setFullName("Nguyen Van A");
        mockUser.setPassword("encoded_password");
        mockUser.setRole("CUSTOMER");
        mockUser.setLoyaltyPoints(0);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setPhone("0987654321");
        request.setPassword("123456");

        when(userRepository.findByPhone(request.getPhone())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(mockUser.getPhone(), mockUser.getRole())).thenReturn("mock_jwt_token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertEquals("mock_jwt_token", response.getToken());
        verify(userRepository, times(1)).findByPhone(request.getPhone());
        verify(passwordEncoder, times(1)).matches(request.getPassword(), mockUser.getPassword());
    }

    @Test
    void testLogin_Failure_UserNotFound() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setPhone("0000000000");
        request.setPassword("123456");

        when(userRepository.findByPhone(request.getPhone())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Số điện thoại hoặc mật khẩu không chính xác!", exception.getMessage());
    }

    @Test
    void testLogin_Failure_WrongPassword() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setPhone("0987654321");
        request.setPassword("wrong_password");

        when(userRepository.findByPhone(request.getPhone())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(request.getPassword(), mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Số điện thoại hoặc mật khẩu không chính xác!", exception.getMessage());
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhone("0123456789");
        request.setPassword("123456");
        request.setFullName("New User");

        when(userRepository.existsByPhone(request.getPhone())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_new_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser); // Trả về mockUser sau khi lưu
        when(jwtTokenProvider.generateToken(anyString(), anyString())).thenReturn("mock_new_jwt_token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("mock_new_jwt_token", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_Failure_PhoneExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhone("0987654321");

        when(userRepository.existsByPhone(request.getPhone())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Số điện thoại này đã được sử dụng!", exception.getMessage());
    }
}
