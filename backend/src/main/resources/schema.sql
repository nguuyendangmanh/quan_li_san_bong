-- Chạy script này để khởi tạo Database
CREATE DATABASE IF NOT EXISTS football_management;
USE football_management;

-- 1. Bảng Người dùng (Tài khoản)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER', -- Phân quyền: CUSTOMER, STAFF, ADMIN
    loyalty_points INT DEFAULT 0, -- [BỔ SUNG] Điểm tích lũy để xét hạng VIP (Thằng số 5 làm)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng Sân bóng
CREATE TABLE fields (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    field_type INT NOT NULL, -- Kích thước sân: 5, 7, 11
    base_price DECIMAL(10, 2) NOT NULL, -- Giá mặc định
    status VARCHAR(20) DEFAULT 'ACTIVE' -- ACTIVE, MAINTENANCE
);

-- 3. Bảng Lịch đặt sân
CREATE TABLE bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    field_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING (Chờ cọc), CONFIRMED (Đã cọc), COMPLETED (Đá xong), CANCELLED (Đã hủy)
    deposit_amount DECIMAL(10, 2) DEFAULT 0, -- [BỔ SUNG] Tiền khách đã cọc trước
    total_price DECIMAL(10, 2), -- Tổng tiền sân (có thể cộng phí giờ vàng/giảm giá VIP)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- [BỔ SUNG] Truy vết thời gian cập nhật trạng thái
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (field_id) REFERENCES fields(id)
);

-- 4. Bảng Dịch vụ/Nước uống
CREATE TABLE services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    status VARCHAR(20) DEFAULT 'AVAILABLE' -- [BỔ SUNG] Trạng thái dịch vụ (AVAILABLE, OUT_OF_STOCK, HIDDEN)
);

-- 5. Bảng Chi tiết dịch vụ đi kèm khi đặt sân (Giỏ hàng nước)
CREATE TABLE booking_services (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL, -- Lưu giá tiền tại thời điểm mua
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (service_id) REFERENCES services(id)
);
