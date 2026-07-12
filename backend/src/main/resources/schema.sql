-- Thiết kế Cấu trúc Database Chuẩn cho Hệ thống Quản lý Sân bóng (Source of Truth)
-- File này được dùng làm tham chiếu gốc để các thành viên đồng bộ cấu trúc DB và Entity

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    loyalty_points INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS football_fields (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    field_type INT NOT NULL, /* Ví dụ: 5, 7, 11 */
    price_per_hour DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    status VARCHAR(50) DEFAULT 'AVAILABLE'
);

CREATE TABLE IF NOT EXISTS price_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    field_id BIGINT NOT NULL,
    start_hour INT NOT NULL,
    end_hour INT NOT NULL,
    multiplier DOUBLE NOT NULL,
    label VARCHAR(100),
    FOREIGN KEY (field_id) REFERENCES football_fields(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    field_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    deposit_amount DECIMAL(10, 2) DEFAULT 0,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (field_id) REFERENCES football_fields(id)
);

CREATE TABLE IF NOT EXISTS services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS booking_services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);
