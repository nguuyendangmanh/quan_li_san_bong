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
│   │   ├── config/           # [TV1] Cấu hình Security JWT, CORS, mã hóa mật khẩu.
│   │   ├── controller/       # Nơi định nghĩa Endpoints công khai API URL.
│   │   ├── service/          # Logic cốt lõi (Tính tiền đa hình, chặn trùng giờ...).
│   │   ├── repository/       # Giao tiếp với MySQL bằng Spring Data JPA.
│   │   ├── entity/           # Class ánh xạ 1-1 với các bảng Database.
│   │   ├── dto/              # [BỔ SUNG] Lớp hứng dữ liệu JSON (LoginRequest, BookingDTO...).
│   │   ├── exception/        # [TV1] Tập trung bắt các lỗi hệ thống và trả về mã lỗi HTTP chuẩn.
│   │   └── FootballApp.java  # File khởi động dự án.
│   │
│   ├── src/main/resources/
│   │   ├── application.properties # Cấu hình kết nối MySQL Port, Username, Password.
│   │   ├── schema.sql        # Câu lệnh tạo 5 bảng Database gốc.
│   │   └── data.sql          # Khởi tạo sẵn tài khoản Admin, Sân mẫu, Nước uống để chạy thử.
│   │
│   └── pom.xml               # Quản lý các dependency Maven.
│
├── frontend/                 # TOÀN BỘ CODE GIAO DIỆN CLIENT (PC WEB THUẦN)
│   ├── components.html       # [TV3 làm] Kho linh kiện mẫu để cả nhóm copy UI (Nút, Bảng, Form).
│   ├── index.html            # Trang chủ Khách hàng xem danh sách sân và bảng giá.
│   ├── login.html            # [TV1] Trang Đăng nhập.
│   ├── register.html         # [TV1] Trang Đăng ký tài khoản bằng Số điện thoại.
│   ├── admin.html            # Khung Layout Dashboard dùng chung (Admin / Staff).
│   │
│   ├── css/
│   │   ├── style.css         # CSS dùng chung toàn hệ thống.
│   │   └── dashboard.css     # CSS riêng cho khu vực quản trị (Sidebar, Menu).
│   │
│   └── js/                   # CHIA ĐỂ TRỊ (Đã chuẩn hóa lại đúng vai trò)
│       ├── api-config.js     # [TV1] Setup hàm fetch() tự động đính kèm Token bảo mật.
│       ├── auth.js           # [TV1] Logic Login/Register, ẩn nút Admin đối với Role STAFF.
│       ├── field.js          # [TV2] Logic Admin Quản lý Sân & thuật toán cấu hình Giá động.
│       ├── booking.js        # [TV3] Logic lưới Đặt lịch, Check-in tại quầy, thuật toán chặn trùng giờ.
│       ├── inventory.js      # [TV4] Logic Nhân viên bán nước, gọi dịch vụ, tự động trừ kho.
│       └── chart.js          # [TV5] Logic Admin xem biểu đồ thống kê doanh thu.
│
└── docs/                     # KHO TÀI LIỆU DỰ ÁN CỦA NHÓM
    ├── README.md             # Hướng dẫn chi tiết cách cài đặt môi trường và chạy dự án.
    ├── Postman_Collection.json # File cấu hình sẵn các link API để cả nhóm dùng chung khi test.
    ├── Diagrams/             # Thư mục chứa các file ảnh sơ đồ UML (Use Case, Class, Sequence, ERD).
    └── BaoCao_CuoiKy.docx    # File Word chứa nội dung báo cáo tiến độ (Mục tiêu đạt >= 100 trang).
```

---

## 🚀 Quy Trình Khởi Chạy

1. **Khởi tạo Local DB:** Mở phần mềm quản lý MySQL, chạy toàn bộ lệnh trong `schema.sql` trước, sau đó chạy tiếp `data.sql` để có dữ liệu gốc test app.
2. **Quy tắc Git:** Mọi người tạo nhánh riêng khi code bằng lệnh: `git checkout -b feature/ten-chuc-nang`. Không commit thẳng lên nhánh `main`. Làm xong gửi Pull Request để gộp code.
