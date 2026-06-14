/**
 * ==========================================
 * XỬ LÝ NGHIỆP VỤ ĐĂNG NHẬP (LOGIN)
 * ==========================================
 * Bắt sự kiện khi người dùng bấm nút "Đăng nhập" trên Form.
 * Sử dụng async/await để gọi API Backend mà không làm đơ trình duyệt.
 */
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // Chặn hành vi tải lại trang (reload) mặc định của trình duyệt
        const phone = document.getElementById('phone').value;
        const password = document.getElementById('password').value;
        const errorDiv = document.getElementById('errorMessage');
        const btn = document.getElementById('loginBtn');

        try {
            btn.innerHTML = 'Đang xử lý...';
            btn.disabled = true;

            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ phone, password })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Đăng nhập thất bại');
            }

            /**
             * BƯỚC QUAN TRỌNG: LƯU TRỮ TOKEN
             * Lưu JWT Token và Quyền (Role) vào LocalStorage của trình duyệt.
             * Các lần gọi API sau (như đặt sân, xem lịch) sẽ lấy Token từ đây để đính kèm vào Header.
             */
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('user_role', data.role);

            /**
             * BƯỚC ĐIỀU HƯỚNG TRANG (ROUTING)
             * Kiểm tra quyền của User trả về từ Backend để chuyển hướng đến trang phù hợp.
             */
            if (data.role === 'ADMIN') {
                window.location.href = 'admin.html'; // Admin vào trang quản trị
            } else if (data.role === 'STAFF') {
                window.location.href = 'staff.html'; // Nhân viên vào trang quản lý
            } else {
                window.location.href = 'index.html'; // Khách hàng quay về trang chủ đặt sân
            }

        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.innerHTML = err.message;
            btn.innerHTML = 'Đăng nhập ngay';
            btn.disabled = false;
        }
    });
}

/**
 * ==========================================
 * XỬ LÝ NGHIỆP VỤ ĐĂNG KÝ (REGISTER)
 * ==========================================
 * Thu thập thông tin từ Form Đăng ký và gửi POST request lên API Backend.
 */
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // Ngăn trình duyệt tự động reload trang
        const fullName = document.getElementById('fullName').value;
        const phone = document.getElementById('phone').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const errorDiv = document.getElementById('errorMessage');
        const btn = document.getElementById('registerBtn');

        try {
            btn.innerHTML = 'Đang tạo tài khoản...';
            btn.disabled = true;

            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fullName, phone, email, password })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || 'Đăng ký thất bại');
            }

            /**
             * ĐĂNG NHẬP TỰ ĐỘNG SAU KHI ĐĂNG KÝ THÀNH CÔNG
             * Lưu ngay Token do Backend cấp phát để duy trì phiên đăng nhập.
             * Giúp tối ưu UX: Khách hàng không cần phải gõ lại mật khẩu để đăng nhập nữa.
             */
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('user_role', data.role);
            window.location.href = 'index.html'; // Chuyển thẳng về trang chủ

        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.innerHTML = err.message;
            btn.innerHTML = 'Đăng ký ngay';
            btn.disabled = false;
        }
    });
}
