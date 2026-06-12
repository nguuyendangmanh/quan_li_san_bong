# ⚽ Hệ Thống Quản Lý Sân Bóng (Football Management System)

Dự án bài tập lớn môn Công nghệ phần mềm.

## ⚙️ Công Nghệ Sử Dụng
- **Ngôn ngữ & Tech Stack:** Java Spring Boot cho Backend (chia 3 lớp chuẩn: Controller, Service, Repository); HTML5, CSS3, Vanilla JS cho Frontend. Cơ sở dữ liệu: MySQL.
- **Mô hình Kiến trúc:** Client-Server tách biệt hoàn toàn qua RESTful API. Trả về định dạng JSON.
- **Tiêu chuẩn Code:** Tên biến, hàm, class, bảng, cột dùng 100% tiếng Anh.
- **Bảo mật & Phân quyền (RBAC):** Có 3 role chính: `ADMIN`, `STAFF`, `CUSTOMER`. Xác thực bằng JWT Token.

---

## 📂 Sơ Đồ Kiến Trúc & Phân Công Chi Tiết (Nhiệm vụ 5 Thành Viên)

```text
football-management-system/
│
├── backend/                  # TOÀN BỘ CODE JAVA SPRING BOOT (RESTful API)
│   ├── src/main/java/com/football/
│   │   ├── config/           # [TV1] Code Security JWT, CORS, cấu hình hệ thống.
│   │   ├── controller/       # Nơi định nghĩa Endpoints (API URL).
│   │   ├── service/          # Nơi code logic tính tiền, chống trùng giờ, trừ kho nước.
│   │   ├── repository/       # Nơi viết câu lệnh truy vấn MySQL bằng Spring Data JPA.
│   │   ├── entity/           # Các Class Map 1-1 với Database: User, Field, Booking, Service.
│   │   └── FootballApp.java  # File khởi động Spring Boot.
│   │
│   ├── src/main/resources/
│   │   ├── application.properties  # Cấu hình Port chạy app, User/Pass MySQL kết nối DB.
│   │   ├── schema.sql        # Script gốc tạo Database 5 bảng cốt lõi.
│   │   └── data.sql          # Script tự động tạo Data giả lập (Admin, Khách VIP, Sân, Nước) để test.
│   │
│   └── pom.xml               # Quản lý thư viện Maven (MySQL, Spring Web, Spring Security, JWT).
│
├── frontend/                 # TOÀN BỘ CODE GIAO DIỆN CLIENT (HTML/CSS/JS THUẦN)
│   ├── index.html            # [TV4] Trang chủ Khách hàng xem danh sách sân.
│   ├── login.html            # [TV1] Trang Đăng nhập. Gọi API trả về JWT Token và Role.
│   ├── register.html         # [TV1] Trang Đăng ký tài khoản (Dành cho Customer).
│   ├── admin.html            # Khung Layout dùng chung cho Dashboard của Staff & Admin.
│   ├── components.html       # File Design System chứa mẫu Nút bấm, Bảng dữ liệu để copy CSS.
│   │
│   ├── css/
│   │   ├── style.css         # Style dùng chung toàn cục.
│   │   └── dashboard.css     # Style dành riêng cho khu vực quản trị (Sidebar, Header).
│   │
│   ├── js/                   # LOGIC FRONT-END
│   │   ├── api-config.js     # [TV1] File setup hàm fetch() chung, tự động nhét JWT Token vào Headers.
│   │   ├── auth.js           # [TV1] Xử lý Form Login/Register, kiểm tra Role để ẩn class .admin-only.
│   │   ├── booking.js        # [TV2] Xử lý Form đặt sân, thanh toán cọc, bắt lỗi nếu API báo trùng lịch.
│   │   ├── field.js          # [TV4] Logic Admin Thêm/Sửa/Xóa (CRUD) cấu hình thông tin Sân và Giá.
│   │   ├── inventory.js      # [TV3] Logic Nhân viên gọi nước uống tại quầy, cập nhật tồn kho.
│   │   └── chart.js          # [TV5] Fetch API lấy data thống kê và vẽ biểu đồ.
│   │
│   └── assets/               # Chứa ảnh minh họa sân, logo, icon.
│
├── docs/                     # KHO TÀI LIỆU DỰ ÁN CỦA NHÓM
│   └── README.md             # [TV5] Nơi gom file Word Báo cáo cuối kỳ, ảnh sơ đồ UML (PlantUML), file Test Cases.
```

---

## 🚀 Quy Trình Khởi Chạy

1. **Khởi tạo Local DB:** Mở phần mềm quản lý MySQL, chạy toàn bộ lệnh trong `schema.sql` trước, sau đó chạy tiếp `data.sql` để có dữ liệu gốc test app.
2. **Quy tắc Git:** Mọi người tạo nhánh riêng khi code bằng lệnh: `git checkout -b feature/ten-chuc-nang`. Không commit thẳng lên nhánh `main`. Làm xong gửi Pull Request để gộp code.
