-- ===========================================================
-- DỮ LIỆU MẪU CHO H2 IN-MEMORY (DÙNG KHI TEST, KHÔNG DÙNG MySQL)
-- H2 không hỗ trợ "INSERT IGNORE" → dùng "INSERT INTO" bình thường
-- (H2 create-drop: mỗi lần khởi động là DB trống → không lo trùng)
-- ===========================================================

-- 2. Insert các Sân bóng mẫu (Thêm address và image_url)
INSERT INTO fields (name, type, base_price_per_hour, status, description, address, image_url) VALUES
('Sân A (5 người)', '5', 200000, 'AVAILABLE', 'Sân có mái che, cỏ nhân tạo mới thay 2023.', '123 Đường bóng đá, Quận 1', 'https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=500&q=80'),
('Sân B (7 người)', '7', 300000, 'AVAILABLE', 'Sân ngoài trời, có khán đài nhỏ.', '123 Đường bóng đá, Quận 1', 'https://images.unsplash.com/photo-1518605368461-1eb5b2d2db3c?w=500&q=80'),
('Sân VIP C (11 người)', '11', 800000, 'MAINTENANCE', 'Sân chuẩn FIFA 11 người, đang bảo dưỡng cỏ.', '456 Đại lộ thể thao, Quận 2', 'https://images.unsplash.com/photo-1459865264687-595d652de67e?w=500&q=80');

-- Mật khẩu BCrypt của chuỗi "123456"
INSERT INTO users (phone, password, full_name, email, role, loyalty_points) VALUES
('0988888888', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyen Van Admin',     'admin@football.vn', 'ADMIN',    0),
('0977777777', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Tran Thi Nhan Vien',   'staff@football.vn', 'STAFF',    0),
('0911111111', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Le Khach VIP',          'vip@football.vn',   'CUSTOMER', 100);
