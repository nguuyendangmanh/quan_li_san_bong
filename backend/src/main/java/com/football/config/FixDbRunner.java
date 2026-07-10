package com.football.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class FixDbRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE bookings DROP FOREIGN KEY bookings_ibfk_2");
            System.out.println("========== SUCCESS: DROPPED bookings_ibfk_2 ==========");
        } catch (Exception e) {
            System.out.println("========== INFO: bookings_ibfk_2 NOT FOUND OR ALREADY DROPPED ==========");
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE bookings DROP FOREIGN KEY bookings_ibfk_1");
            System.out.println("========== SUCCESS: DROPPED bookings_ibfk_1 ==========");
        } catch (Exception e) {
            System.out.println("========== INFO: bookings_ibfk_1 NOT FOUND OR ALREADY DROPPED ==========");
        }
    }
}
