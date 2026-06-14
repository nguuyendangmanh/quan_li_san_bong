// Xử lý form đăng nhập
const loginForm = document.getElementById('loginForm');
if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
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

            // Lưu JWT Token
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('user_role', data.role);

            // Phân luồng trang dựa trên Role
            if (data.role === 'ADMIN') {
                window.location.href = 'admin.html';
            } else if (data.role === 'STAFF') {
                window.location.href = 'staff.html';
            } else {
                window.location.href = 'index.html';
            }

        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.innerHTML = err.message;
            btn.innerHTML = 'Đăng nhập ngay';
            btn.disabled = false;
        }
    });
}

// Xử lý form đăng ký
const registerForm = document.getElementById('registerForm');
if (registerForm) {
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
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

            // Lưu JWT Token và cho vào trang luôn
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('user_role', data.role);
            window.location.href = 'index.html';

        } catch (err) {
            errorDiv.style.display = 'block';
            errorDiv.innerHTML = err.message;
            btn.innerHTML = 'Đăng ký ngay';
            btn.disabled = false;
        }
    });
}
