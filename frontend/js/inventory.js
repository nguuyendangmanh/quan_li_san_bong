/**
 * ==============================================================================
 * FRONTEND JAVASCRIPT: QUẢN LÝ DỊCH VỤ & KHO HÀNG (TV4)
 * ==============================================================================
 * File này xử lý các hoạt động CRUD Dịch vụ/Nước uống và Đặt thêm dịch vụ tại quầy.
 * Sử dụng hàm fetchAPI() từ api-config.js để tự động gửi kèm JWT Token.
 */

// Đợi DOM load xong mới thực thi để tránh lỗi thẻ HTML chưa được tạo
document.addEventListener('DOMContentLoaded', () => {
    // 1. Phân quyền giao diện (Ẩn các phần chỉ dành cho Admin nếu user không phải ADMIN)
    applyRoleBasedUI();

    // 2. Tự động load danh sách dịch vụ nếu trang có bảng hiển thị
    if (document.getElementById('servicesTable')) {
        loadAndRenderServices();
    }

    // 3. Tự động lắng nghe sự kiện submit form thêm/sửa dịch vụ
    const serviceForm = document.getElementById('serviceForm');
    if (serviceForm) {
        serviceForm.addEventListener('submit', handleServiceFormSubmit);
    }
});

/**
 * Áp dụng phân quyền giao diện phía Client.
 * Ẩn tất cả các thẻ có class 'admin-only' nếu người dùng hiện tại không phải ADMIN.
 */
function applyRoleBasedUI() {
    const userRole = localStorage.getItem('user_role');
    if (userRole !== 'ADMIN') {
        const adminElements = document.querySelectorAll('.admin-only');
        adminElements.forEach(el => {
            el.style.setProperty('display', 'none', 'important');
        });
    }
}

/**
 * Gọi API lấy danh sách dịch vụ và hiển thị lên bảng HTML (servicesTable)
 */
async function loadAndRenderServices() {
    const tableBody = document.querySelector('#servicesTable tbody');
    if (!tableBody) return;

    try {
        tableBody.innerHTML = '<tr><td colspan="5">Đang tải dữ liệu...</td></tr>';
        
        // Gọi API của TV4: GET /api/services
        const services = await fetchAPI('/services');
        if (!services) return;

        tableBody.innerHTML = ''; // Xóa dữ liệu cũ

        if (services.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="5">Không có dịch vụ/nước uống nào.</td></tr>';
            return;
        }

        // Tạo danh sách dòng hiển thị dịch vụ
        services.forEach(service => {
            const tr = document.createElement('tr');
            
            // Xác định màu sắc cảnh báo tồn kho nếu gần hết hàng
            const stockWarningClass = service.stockQuantity <= 5 ? 'text-danger font-weight-bold' : '';

            tr.innerHTML = `
                <td>${service.id}</td>
                <td>${service.name}</td>
                <td>${formatCurrency(service.price)}</td>
                <td class="${stockWarningClass}">${service.stockQuantity}</td>
                <td>
                    <button class="btn btn-primary btn-sm admin-only" onclick="editService(${service.id}, '${service.name}', ${service.price}, ${service.stockQuantity})">Sửa</button>
                    <button class="btn btn-danger btn-sm admin-only" onclick="deleteService(${service.id})">Xóa</button>
                </td>
            `;
            tableBody.appendChild(tr);
        });

        // Áp dụng lại ẩn/hiển các nút bấm Admin vừa được render động
        applyRoleBasedUI();

    } catch (error) {
        console.error('Lỗi khi tải danh sách dịch vụ:', error);
        tableBody.innerHTML = '<tr><td colspan="5" class="text-danger">Lỗi tải dữ liệu từ máy chủ.</td></tr>';
    }
}

/**
 * Xử lý khi nhấn nút Thêm hoặc Lưu cập nhật dịch vụ
 */
async function handleServiceFormSubmit(e) {
    e.preventDefault();

    const idInput = document.getElementById('serviceId'); // ID ẩn để biết là Sửa hay Thêm mới
    const nameInput = document.getElementById('serviceName');
    const priceInput = document.getElementById('servicePrice');
    const stockInput = document.getElementById('serviceStock');
    const errorDiv = document.getElementById('formErrorMessage');

    const serviceData = {
        name: nameInput.value,
        price: parseFloat(priceInput.value),
        stockQuantity: parseInt(stockInput.value)
    };

    try {
        let response;
        if (idInput && idInput.value) {
            // Nghiệp vụ SỬA: PUT /api/services/{id}
            response = await fetchAPI(`/services/${idInput.value}`, {
                method: 'PUT',
                body: JSON.stringify(serviceData)
            });
        } else {
            // Nghiệp vụ THÊM MỚI: POST /api/services
            response = await fetchAPI('/services', {
                method: 'POST',
                body: JSON.stringify(serviceData)
            });
        }

        if (response) {
            alert('Lưu thông tin dịch vụ thành công!');
            // Reset form và reload bảng dữ liệu
            serviceForm.reset();
            if (idInput) idInput.value = '';
            loadAndRenderServices();
        }
    } catch (err) {
        if (errorDiv) {
            errorDiv.style.display = 'block';
            errorDiv.innerHTML = err.message || 'Thao tác thất bại!';
        } else {
            alert(err.message || 'Thao tác thất bại!');
        }
    }
}

/**
 * Đổ dữ liệu dịch vụ lên form để chuẩn bị chỉnh sửa (được gọi từ nút Sửa trong bảng)
 */
window.editService = function(id, name, price, stock) {
    const idInput = document.getElementById('serviceId');
    const nameInput = document.getElementById('serviceName');
    const priceInput = document.getElementById('servicePrice');
    const stockInput = document.getElementById('serviceStock');

    if (idInput) idInput.value = id;
    if (nameInput) nameInput.value = name;
    if (priceInput) priceInput.value = price;
    if (stockInput) stockInput.value = stock;

    // Cuộn trang lên form nhập liệu
    const formContainer = document.getElementById('serviceFormContainer');
    if (formContainer) {
        formContainer.scrollIntoView({ behavior: 'smooth' });
    }
};

/**
 * Thực hiện xóa dịch vụ (Admin)
 */
window.deleteService = async function(id) {
    if (!confirm('Bạn có chắc chắn muốn xóa dịch vụ này không?')) return;

    try {
        // API: DELETE /api/services/{id}
        const response = await fetchAPI(`/services/${id}`, {
            method: 'DELETE'
        });

        if (response) {
            alert('Xóa dịch vụ thành công!');
            loadAndRenderServices();
        }
    } catch (error) {
        alert(error.message || 'Xóa dịch vụ thất bại!');
    }
};

/**
 * API Gọi nước uống tại quầy cho khách và trừ tồn kho tự động (Staff/Admin)
 * Hàm này dùng để gọi từ Dashboard đặt sân của Nhân viên
 * @param {number} bookingId ID của lượt đặt sân cần gọi thêm dịch vụ
 * @param {Array<{serviceId: number, quantity: number}>} items Mảng chứa danh sách nước gọi
 */
window.orderServicesForBooking = async function(bookingId, items) {
    try {
        const payload = {
            bookingId: bookingId,
            items: items
        };

        // API: POST /api/services/order
        const response = await fetchAPI('/services/order', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        if (response) {
            alert('Gọi thêm nước uống/dịch vụ thành công!');
            return true;
        }
    } catch (error) {
        alert(error.message || 'Lỗi khi gọi dịch vụ!');
        return false;
    }
};

/**
 * Định dạng số tiền sang chuẩn VNĐ để hiển thị lên UI
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
}
