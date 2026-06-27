async function loadCustomers() {
    const tbody = document.getElementById('customer-tbody');
    tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Đang tải dữ liệu...</td></tr>';

    try {
        // Sử dụng hàm fetchAPI dùng chung của nhóm từ api-config.js
        const data = await fetchAPI('/customers');
        if (data) {
            renderCustomerTable(data);
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="5" style="color:red; text-align:center;">Lỗi: ${err.message}</td></tr>`;
    }
}

function renderCustomerTable(customers) {
    const tbody = document.getElementById('customer-tbody');
    if (!customers.length) {
        tbody.innerHTML = '<tr><td colspan="5">Chưa có dữ liệu.</td></tr>';
        return;
    }
    tbody.innerHTML = customers.map(c => `
        <tr>
            <td>${c.id}</td>
            <td>${c.fullName}</td>
            <td>${c.phoneNumber}</td>
            <td>${c.loyaltyPoints}</td>
            <td><span class="badge badge-${c.vipTier.toLowerCase()}">${formatVipTier(c.vipTier)}</span></td>
        </tr>
    `).join('');
}

function formatVipTier(tier) {
    const map = {
        DONG: '🥉 Đồng',
        BAC: '🥈 Bạc',
        VANG: '🥇 Vàng',
        KIMCUONG: '💎 Kim Cương'
    };
    return map[tier] || tier;
}

document.addEventListener('DOMContentLoaded', loadCustomers);
