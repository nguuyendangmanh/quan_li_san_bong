-- Dữ liệu giả lập ban đầu để nhóm có data test ngay lập tức
-- (Bỏ USE vì H2 không hỗ trợ, password đã mã hóa BCrypt của '123456')

-- 1. Insert 3 tài khoản mẫu
INSERT IGNORE INTO users (phone, password, full_name, email, role) VALUES
('0988888888', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyễn Văn Admin', 'admin@football.vn', 'ADMIN'),
('0977777777', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Trần Thị Nhân Viên', 'staff@football.vn', 'STAFF'),
('0911111111', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Lê Khách VIP', 'vip@football.vn', 'CUSTOMER');
