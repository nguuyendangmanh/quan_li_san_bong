package com.football.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
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

        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS payments");
            System.out.println("========== SUCCESS: DROPPED payments table ==========");
        } catch (Exception e) {
            System.out.println("========== INFO: payments NOT FOUND OR DROP FAILED ==========");
        }

        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_managed_fields (user_id BIGINT NOT NULL, field_id INT NOT NULL, PRIMARY KEY (user_id, field_id))");
            System.out.println("========== SUCCESS: CREATED user_managed_fields ==========");
        } catch (Exception e) {
            System.out.println("========== INFO: user_managed_fields creation failed: " + e.getMessage() + " ==========");
        }
    }
}
