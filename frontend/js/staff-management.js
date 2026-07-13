function openStaffModal() {
    document.getElementById('staffModal').style.display = 'flex';
    document.getElementById('staffError').style.display = 'none';
}

function closeStaffModal() {
    document.getElementById('staffModal').style.display = 'none';
    // Clear inputs
    document.getElementById('staffName').value = '';
    document.getElementById('staffPhone').value = '';
    document.getElementById('staffEmail').value = '';
    document.getElementById('staffPassword').value = '';
}

async function loadStaffList() {
    try {
        const response = await fetchAPI('/api/users/staff');
        const staffs = await response.json();
        
        const tbody = document.getElementById('staff-tbody');
        if (staffs.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Chưa có nhân viên nào</td></tr>';
            return;
        }

        tbody.innerHTML = staffs.map(s => `
            <tr>
                <td>#${s.id}</td>
                <td style="font-weight:600; color:var(--text-main);">${s.fullName}</td>
                <td>${s.phone}</td>
                <td>${s.email || '-'}</td>
                <td><span class="badge">NHÂN VIÊN</span></td>
            </tr>
        `).join('');

    } catch (error) {
        console.error("Lỗi khi tải danh sách nhân viên:", error);
    }
}

async function createStaff() {
    const fullName = document.getElementById('staffName').value;
    const phone = document.getElementById('staffPhone').value;
    const email = document.getElementById('staffEmail').value;
    const password = document.getElementById('staffPassword').value;

    if (!fullName || !phone || !password) {
        const errDiv = document.getElementById('staffError');
        errDiv.innerText = "Vui lòng nhập đầy đủ Tên, SĐT và Mật khẩu!";
        errDiv.style.display = 'block';
        return;
    }

    try {
        const response = await fetchAPI('/api/users/staff', {
            method: 'POST',
            body: JSON.stringify({ fullName, phone, email, password })
        });
        
        if (response.ok) {
            alert("Tạo tài khoản nhân viên thành công!");
            closeStaffModal();
            loadStaffList(); // Refresh list
        } else {
            const err = await response.json();
            const errDiv = document.getElementById('staffError');
            errDiv.innerText = err.error || "Lỗi tạo tài khoản!";
            errDiv.style.display = 'block';
        }
    } catch (error) {
        console.error("Lỗi tạo nhân viên:", error);
        alert("Có lỗi xảy ra, vui lòng thử lại!");
    }
}

// Chạy load danh sách khi vào trang
document.addEventListener("DOMContentLoaded", loadStaffList);
