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
        const staffs = await fetchAPI('/api/users/staff');
        
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

async function loadFields() {
    try {
        const fields = await fetchAPI('/api/fields');
        const container = document.getElementById('staffFieldsList');
        
        if (fields.length === 0) {
            container.innerHTML = '<div style="color:var(--text-muted); font-size:13px;">Chưa có sân nào trong hệ thống</div>';
            return;
        }

        container.innerHTML = fields.map(f => `
            <label style="display:flex; align-items:center; gap:8px; font-size:14px; color:var(--text-main); cursor:pointer;">
                <input type="checkbox" name="managedFields" value="${f.id}" style="width:16px; height:16px; cursor:pointer;">
                ${f.name} (Loại: ${f.type} người)
            </label>
        `).join('');
    } catch (error) {
        console.error("Lỗi khi tải danh sách sân:", error);
        document.getElementById('staffFieldsList').innerHTML = '<div style="color:#ef4444; font-size:13px;">Lỗi tải dữ liệu sân</div>';
    }
}

async function createStaff() {
    const fullName = document.getElementById('staffName').value;
    const phone = document.getElementById('staffPhone').value;
    const email = document.getElementById('staffEmail').value;
    const password = document.getElementById('staffPassword').value;

    // Lấy danh sách ID sân được chọn
    const checkboxes = document.querySelectorAll('input[name="managedFields"]:checked');
    const managedFieldIds = Array.from(checkboxes).map(cb => parseInt(cb.value));

    if (!fullName || !phone || !password) {
        const errDiv = document.getElementById('staffError');
        errDiv.innerText = "Vui lòng nhập đầy đủ Tên, SĐT và Mật khẩu!";
        errDiv.style.display = 'block';
        return;
    }

    if (managedFieldIds.length === 0) {
        const errDiv = document.getElementById('staffError');
        errDiv.innerText = "Vui lòng gán ít nhất 1 sân cho nhân viên!";
        errDiv.style.display = 'block';
        return;
    }

    try {
        const response = await fetchAPI('/api/users/staff', {
            method: 'POST',
            body: JSON.stringify({ fullName, phone, email, password, managedFieldIds })
        });
        
        if (response && response.error) { // Lỗi 400 Bad Request
            const errDiv = document.getElementById('staffError');
            errDiv.innerText = response.error;
            errDiv.style.display = 'block';
        } else {
            alert("Tạo tài khoản nhân viên thành công!");
            closeStaffModal();
            loadStaffList(); // Refresh list
        }
    } catch (error) {
        console.error("Lỗi tạo nhân viên:", error);
        alert("Có lỗi xảy ra, vui lòng thử lại!");
    }
}

// Chạy load danh sách khi vào trang
document.addEventListener("DOMContentLoaded", () => {
    loadStaffList();
    loadFields();
});
