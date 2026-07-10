package com.football;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FootballApp {
    public static void main(String[] args) {
        SpringApplication.run(FootballApp.class, args);
        System.out.println("🚀 [BACKEND READY] Hệ thống quản lý sân bóng đã chạy tại Port 8080!");
    }
}
