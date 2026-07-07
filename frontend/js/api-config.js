// Tất cả các thành viên khi gọi API phải nối thêm biến này ở đầu
const API_BASE_URL = 'http://localhost:8080/api';

// [TV1] Hàm gọi API fetch() chung, tự động nhét Token vào Header
async function fetchAPI(endpoint, options = {}) {
    // 1. Cấu hình Header mặc định là JSON
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    // 2. Lấy Token từ LocalStorage (TV1 sẽ lưu cái này khi Đăng nhập)
    const token = localStorage.getItem('jwt_token');
    if (token) {
        headers['Authorization'] = 'Bearer ' + token; // Nhét token vào để Server check
    }

    const config = {
        ...options,
        headers: headers
    };

    // 3. Gọi API
    try {
        const response = await fetch(API_BASE_URL + endpoint, config);
        
        // 4. Bắt lỗi 401: Chưa đăng nhập hoặc token hết hạn
        if (response.status === 401) {
            alert('Phiên đăng nhập hết hạn hoặc bạn không có quyền. Vui lòng đăng nhập lại!');
            window.location.href = 'login.html';
            return null;
        }

        // 5. Trả dữ liệu JSON cho các TV khác dùng
        const text = await response.text();
        if (!text) {
            return {}; // Trả về object rỗng nếu API không có nội dung trả về (ví dụ DELETE - 204 No Content)
        }
        const data = JSON.parse(text);
        return data;
    } catch (error) {
        console.error('Lỗi khi gọi API:', error);
        throw error;
    }
}
