-- Dữ liệu giả lập ban đầu để nhóm có data test ngay lập tức
USE football_management;

-- 1. Insert 3 tài khoản mẫu
INSERT INTO users (phone_number, password, full_name, role) VALUES 
('0988888888', '123456', 'Nguyễn Văn Admin', 'ADMIN'),
('0977777777', '123456', 'Trần Thị Nhân Viên', 'STAFF'),
('0911111111', '123456', 'Lê Khách VIP', 'CUSTOMER');

-- Cập nhật điểm cho khách VIP
UPDATE users SET loyalty_points = 500 WHERE phone_number = '0911111111';

-- 2. Insert các sân bóng mẫu
INSERT INTO fields (name, field_type, base_price) VALUES 
('Sân Bóng A1 (5 người)', 5, 250000.00),
('Sân Bóng A2 (5 người)', 5, 250000.00),
('Sân Bóng B1 (7 người)', 7, 400000.00),
('Sân Bóng C1 (11 người)', 11, 800000.00);

-- 3. Insert Dịch vụ (Nước uống) mẫu
INSERT INTO services (name, price, stock_quantity) VALUES 
('Nước khoáng Revive', 15000.00, 100),
('Bò húc Redbull', 20000.00, 50),
('Trà đá (Bình)', 30000.00, 20);
