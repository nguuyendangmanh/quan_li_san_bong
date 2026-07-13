package com.football.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    // Khóa bí mật mã hóa Token (Cố định để không bị lỗi 403 mỗi lần restart server)
    private final Key key = Keys.hmacShaKeyFor("Th1sIs4V3ryS3cr3tK3yF0rJ0wtT0k3nS1gn4tur3!".getBytes());
    private final long JWT_EXPIRATION = 86400000L; // Sống 24 giờ

    // Sinh Token khi Đăng nhập thành công
    public String generateToken(String phone, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(phone) // Username là SĐT
                .claim("role", role) // Nhét Role vào Token
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // Lấy SĐT từ chuỗi Token
    public String getPhoneFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getRoleFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("role");
    }

    // Kiểm tra Token còn hạn và hợp lệ không
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
