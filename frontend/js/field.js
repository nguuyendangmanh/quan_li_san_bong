/**
 * ============================================================
 * FIELD.JS – QUẢN LÝ SÂN BÓNG (TV2)
 * ============================================================
 * Module JavaScript xử lý toàn bộ giao diện Quản lý Sân bóng:
 *   1. Hiển thị danh sách sân (bảng table-base từ components.html)
 *   2. CRUD: Thêm / Sửa / Xóa sân
 *   3. Cấu hình khung giờ vàng (PriceConfig)
 *   4. Preview tính giá tự động theo thuật toán đa hình
 *
 * Yêu cầu: File này phải được include SAU api-config.js
 * (vì dùng hàm fetchAPI() và biến API_BASE_URL từ api-config.js)
 */

// ============================================================
// BIẾN TOÀN CỤC – LƯU TRẠNG THÁI HIỆN TẠI CỦA MODULE
// ============================================================

/** Lưu ID sân đang được chỉnh sửa. null = đang ở chế độ Tạo mới */
let currentEditingFieldId = null;

/** Cache danh sách sân (để dùng lại, tránh gọi API nhiều lần) */
let fieldsCache = [];

// ============================================================
// PHẦN 1: KHỞI TẠO VÀ TẢI DỮ LIỆU
// ============================================================

/**
 * Hàm khởi tạo module – gọi ngay khi trang được load.
 * Kiểm tra đăng nhập và tải danh sách sân bóng từ API.
 */
async function initFieldModule() {
    // [TẠM TẮT] TV1 đã tắt bảo mật API Backend để test, nên Frontend cũng tạm tắt check Token
    /*
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        alert('Bạn chưa đăng nhập. Vui lòng đăng nhập để tiếp tục!');
        window.location.href = 'login.html';
        return;
    }
    */
    
    // Tải danh sách sân ngay khi vào trang
    await loadFields();
}

/**
 * Tải và hiển thị danh sách tất cả sân bóng từ API.
 * Render ra bảng HTML dùng class "table-base" từ components.html.
 */
async function loadFields() {
    try {
        showLoadingState(true);

        // Gọi API GET /api/fields (dùng hàm fetchAPI() chung từ api-config.js)
        const fields = await fetchAPI('/fields');

        if (!fields || fields.error) {
            showError(fields?.error || 'Không thể tải danh sách sân bóng!');
            return;
        }

        // Lưu vào cache để dùng lại
        fieldsCache = fields;

        // Render bảng danh sách sân ra màn hình
        renderFieldTable(fields);

    } catch (error) {
        showError('Lỗi kết nối đến Server: ' + error.message);
    } finally {
        showLoadingState(false);
    }
}

/**
 * Render bảng danh sách sân bóng ra phần tử HTML có id="fieldTableBody".
 * Dùng class "table-base", "btn", "btn-primary", "btn-danger" từ components.html.
 * @param {Array} fields Danh sách FieldResponseDTO từ API
 */
function renderFieldTable(fields) {
    // Lấy phần tử <tbody> trong bảng để điền dữ liệu vào
    const tbody = document.getElementById('fieldTableBody');
    if (!tbody) return; // Guard: thoát nếu phần tử không tồn tại trên trang

    // Nếu không có sân nào, hiển thị thông báo
    if (fields.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align:center; color:#888;">
            Chưa có sân bóng nào. Hãy thêm sân đầu tiên!</td></tr>`;
        return;
    }

    // Tạo các dòng HTML cho từng sân
    tbody.innerHTML = fields.map(field => `
        <tr id="field-row-${field.id}">
            <td>${field.id}</td>
            <td><strong>${field.name}</strong></td>
            <td>${formatFieldType(field.type)}</td>
            <td>${formatCurrency(field.basePricePerHour)}</td>
            <td>
                <span class="status-badge status-${field.status.toLowerCase()}">
                    ${formatStatus(field.status)}
                </span>
            </td>
            <td>
                <!-- Nút Cấu hình Giờ Vàng -->
                <button class="btn btn-price" onclick="openPriceConfigModal(${field.id})"
                        id="btn-price-${field.id}" title="Cấu hình giờ vàng">
                    ⏰ Giờ Vàng
                </button>
                <!-- Nút Tính Giá Preview -->
                <button class="btn btn-calc" onclick="openCalculateModal(${field.id})"
                        id="btn-calc-${field.id}" title="Tính giá thử">
                    🧮 Tính Giá
                </button>
                <!-- Nút Sửa -->
                <button class="btn btn-primary" onclick="openEditModal(${field.id})"
                        id="btn-edit-${field.id}">
                    ✏️ Sửa
                </button>
                <!-- Nút Xóa -->
                <button class="btn btn-danger" onclick="deleteField(${field.id})"
                        id="btn-delete-${field.id}">
                    🗑️ Xóa
                </button>
            </td>
        </tr>
    `).join('');
}

// ============================================================
// PHẦN 2: THÊM SÂN MỚI (CREATE)
// ============================================================

/**
 * Mở modal form Thêm sân mới.
 * Reset form và đặt chế độ về "Tạo mới" (currentEditingFieldId = null).
 */
function openCreateModal() {
    // Đánh dấu chế độ tạo mới (không phải sửa)
    currentEditingFieldId = null;

    // Đặt tiêu đề modal
    const modalTitle = document.getElementById('fieldModalTitle');
    if (modalTitle) modalTitle.textContent = 'Thêm Sân Bóng Mới';

    // Reset toàn bộ form về trống
    resetFieldForm();

    // Hiển thị modal
    showModal('fieldModal');
}

// ============================================================
// PHẦN 3: SỬA SÂN (UPDATE)
// ============================================================

/**
 * Mở modal form Sửa sân bóng đã có.
 * Tải thông tin sân từ API và điền vào form.
 * @param {number} fieldId ID của sân cần sửa
 */
async function openEditModal(fieldId) {
    try {
        // Đặt chế độ "Đang sửa" và lưu ID
        currentEditingFieldId = fieldId;

        // Đặt tiêu đề modal
        const modalTitle = document.getElementById('fieldModalTitle');
        if (modalTitle) modalTitle.textContent = 'Chỉnh Sửa Thông Tin Sân';

        // Gọi API lấy chi tiết sân theo ID
        const field = await fetchAPI(`/fields/${fieldId}`);
        if (!field || field.error) {
            showError(field?.error || 'Không thể tải thông tin sân!');
            return;
        }

        // Điền thông tin sân vào các ô input trong form
        populateFieldForm(field);

        // Hiển thị modal
        showModal('fieldModal');

    } catch (error) {
        showError('Lỗi: ' + error.message);
    }
}

/**
 * Điền dữ liệu sân bóng vào các ô input trong form.
 * @param {Object} field FieldResponseDTO từ API
 */
function populateFieldForm(field) {
    setFieldValue('fieldName', field.name);
    setFieldValue('fieldType', field.type);
    setFieldValue('fieldBasePrice', field.basePricePerHour);
    setFieldValue('fieldStatus', field.status);
    setFieldValue('fieldDescription', field.description || '');
    setFieldValue('fieldAddress', field.address || '');
    setFieldValue('fieldImageUrl', field.imageUrl || '');

    // Hiển thị danh sách giờ vàng đã cấu hình trong form
    renderPriceConfigInputs(field.priceConfigs || []);
}

// ============================================================
// PHẦN 4: LƯU SÂN (CREATE hoặc UPDATE tùy chế độ)
// ============================================================

/**
 * Thu thập dữ liệu từ form và gửi lên API để tạo mới hoặc cập nhật sân.
 * Tự động phân biệt chế độ dựa vào biến currentEditingFieldId.
 */
async function saveField() {
    // Lấy dữ liệu từ các ô input trong form
    const requestData = {
        name:              getFieldValue('fieldName'),
        type:              getFieldValue('fieldType'),
        basePricePerHour:  parseFloat(getFieldValue('fieldBasePrice')),
        status:            getFieldValue('fieldStatus'),
        description:       getFieldValue('fieldDescription'),
        address:           getFieldValue('fieldAddress'),
        imageUrl:          getFieldValue('fieldImageUrl'),
        priceConfigs:      collectPriceConfigInputs() // Thu thập cấu hình giờ vàng
    };

    // Validate dữ liệu cơ bản trước khi gửi API
    if (!requestData.name || !requestData.type || !requestData.basePricePerHour) {
        showError('Vui lòng điền đầy đủ: Tên sân, Loại sân và Giá thuê!');
        return;
    }
    if (requestData.basePricePerHour <= 0) {
        showError('Giá thuê phải lớn hơn 0!');
        return;
    }

    const saveBtn = document.getElementById('saveFieldBtn');
    if (saveBtn) {
        saveBtn.textContent = 'Đang lưu...';
        saveBtn.disabled = true;
    }

    try {
        let result;

        if (currentEditingFieldId === null) {
            // Chế độ TẠO MỚI: Gọi POST /api/fields
            result = await fetchAPI('/fields', {
                method: 'POST',
                body: JSON.stringify(requestData)
            });
        } else {
            // Chế độ CẬP NHẬT: Gọi PUT /api/fields/{id}
            result = await fetchAPI(`/fields/${currentEditingFieldId}`, {
                method: 'PUT',
                body: JSON.stringify(requestData)
            });
        }

        // Kiểm tra lỗi trả về từ API
        if (result && result.error) {
            showError(result.error);
            return;
        }

        // Thành công: Đóng modal và tải lại danh sách
        hideModal('fieldModal');
        showSuccess(currentEditingFieldId ? 'Cập nhật sân thành công!' : 'Thêm sân mới thành công!');
        await loadFields(); // Reload bảng dữ liệu

    } catch (error) {
        showError('Lỗi khi lưu sân: ' + error.message);
    } finally {
        // Khôi phục nút Save dù thành công hay thất bại
        if (saveBtn) {
            saveBtn.textContent = 'Lưu';
            saveBtn.disabled = false;
        }
    }
}

// ============================================================
// PHẦN 5: XÓA SÂN (DELETE)
// ============================================================

/**
 * Xóa sân bóng khỏi hệ thống.
 * Hỏi xác nhận người dùng trước khi thực hiện.
 * @param {number} fieldId ID của sân cần xóa
 */
async function deleteField(fieldId) {
    // Tìm tên sân từ cache để hiển thị trong hộp thoại xác nhận
    const field = fieldsCache.find(f => f.id === fieldId);
    const fieldName = field ? `"${field.name}"` : `#${fieldId}`;

    // Hỏi xác nhận: Tránh xóa nhầm
    const confirmed = confirm(`⚠️ Bạn có chắc muốn XÓA sân ${fieldName}?\n\nHành động này KHÔNG THỂ hoàn tác!`);
    if (!confirmed) return;

    try {
        // Gọi API DELETE /api/fields/{id}
        await fetchAPI(`/fields/${fieldId}`, { method: 'DELETE' });

        // Thành công: Thông báo và cập nhật lại bảng
        showSuccess(`Đã xóa sân ${fieldName} thành công!`);
        await loadFields(); // Reload bảng

    } catch (error) {
        showError('Không thể xóa sân: ' + error.message);
    }
}

// ============================================================
// PHẦN 6: QUẢN LÝ GIỜ VÀNG (PRICE CONFIG)
// ============================================================

/**
 * Mở modal quản lý cấu hình giờ vàng cho 1 sân.
 * Hiển thị danh sách các khung giờ đặc biệt đã được cấu hình.
 * @param {number} fieldId ID của sân cần xem/sửa cấu hình giá
 */
async function openPriceConfigModal(fieldId) {
    try {
        // Lấy thông tin sân từ cache
        const field = fieldsCache.find(f => f.id === fieldId);
        const fieldName = field ? field.name : `Sân #${fieldId}`;

        // Đặt tiêu đề modal
        const title = document.getElementById('priceConfigModalTitle');
        if (title) title.textContent = `Cấu hình Giờ Vàng – ${fieldName}`;

        // Gọi API lấy danh sách giờ vàng của sân này
        const configs = await fetchAPI(`/fields/${fieldId}/price-configs`);

        // Render danh sách giờ vàng ra modal
        renderPriceConfigList(fieldId, configs || []);

        // Hiển thị modal
        showModal('priceConfigModal');

    } catch (error) {
        showError('Không thể tải cấu hình giá: ' + error.message);
    }
}

/**
 * Render danh sách cấu hình giờ vàng trong modal.
 * Mỗi khung giờ là 1 hàng trong bảng, có thể sửa inline.
 * @param {number} fieldId ID của sân
 * @param {Array}  configs Danh sách PriceConfigDTO từ API
 */
function renderPriceConfigList(fieldId, configs) {
    const container = document.getElementById('priceConfigList');
    if (!container) return;

    if (configs.length === 0) {
        container.innerHTML = `<p style="color:#888; text-align:center;">
            Chưa có cấu hình giờ vàng nào. Sân này đang dùng giá gốc cố định.</p>`;
        return;
    }

    // Tạo bảng hiển thị các khung giờ đặc biệt
    container.innerHTML = `
        <table class="table-base">
            <thead>
                <tr>
                    <th>Nhãn</th>
                    <th>Giờ Bắt đầu</th>
                    <th>Giờ Kết thúc</th>
                    <th>Hệ số × (Multiplier)</th>
                    <th>Giá tăng thêm</th>
                </tr>
            </thead>
            <tbody>
                ${configs.map(cfg => `
                    <tr>
                        <td><strong>${cfg.label || 'Giờ Vàng'}</strong></td>
                        <td>${cfg.startHour}:00</td>
                        <td>${cfg.endHour}:00</td>
                        <td>×${cfg.multiplier} 
                            <span style="color:#e67e22; font-size:0.85em;">
                                (+${((cfg.multiplier - 1) * 100).toFixed(0)}%)
                            </span>
                        </td>
                        <td style="color:#27ae60; font-weight:bold;">
                            Tăng ${((cfg.multiplier - 1) * 100).toFixed(0)}% so với giá gốc
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
        <p style="margin-top:10px; font-size:0.85em; color:#888;">
            💡 Để thay đổi cấu hình giờ vàng, bấm <strong>Sửa</strong> sân và chỉnh sửa trong form chính.
        </p>
    `;
}

// ============================================================
// PHẦN 7: TÍNH GIÁ PREVIEW (DYNAMIC PRICING DEMO)
// ============================================================

/**
 * Mở modal tính giá thử nghiệm cho 1 sân cụ thể.
 * Cho phép người dùng chọn giờ bắt đầu/kết thúc và xem tổng tiền.
 * @param {number} fieldId ID của sân muốn tính giá
 */
function openCalculateModal(fieldId) {
    // Lưu fieldId vào hidden input để dùng khi bấm tính giá
    setFieldValue('calcFieldId', fieldId);

    // Tìm tên sân từ cache
    const field = fieldsCache.find(f => f.id === fieldId);
    const calcTitle = document.getElementById('calcModalTitle');
    if (calcTitle) calcTitle.textContent = `Tính Giá – ${field ? field.name : 'Sân #' + fieldId}`;

    // Reset kết quả cũ
    const resultDiv = document.getElementById('calcResult');
    if (resultDiv) resultDiv.innerHTML = '';

    showModal('calcModal');
}

/**
 * ★ Gọi thuật toán tính giá đa hình từ Backend và hiển thị kết quả ★
 * Nhận dữ liệu từ form (fieldId, startHour, endHour), gửi lên API,
 * sau đó render bảng breakdown giá từng giờ.
 */
async function calculatePrice() {
    const fieldId   = parseInt(getFieldValue('calcFieldId'));
    const startHour = parseInt(getFieldValue('calcStartHour'));
    const endHour   = parseInt(getFieldValue('calcEndHour'));

    // Validate đầu vào trước khi gọi API
    if (isNaN(startHour) || isNaN(endHour)) {
        showError('Vui lòng nhập giờ bắt đầu và kết thúc!');
        return;
    }
    if (endHour <= startHour) {
        showError('Giờ kết thúc phải sau giờ bắt đầu!');
        return;
    }

    const calcBtn = document.getElementById('calcBtn');
    if (calcBtn) {
        calcBtn.textContent = 'Đang tính...';
        calcBtn.disabled = true;
    }

    try {
        // Gọi API POST /api/fields/calculate-price
        const result = await fetchAPI('/fields/calculate-price', {
            method: 'POST',
            body: JSON.stringify({ fieldId, startHour, endHour })
        });

        if (result && result.error) {
            showError(result.error);
            return;
        }

        // Hiển thị kết quả chi tiết lên giao diện
        renderPriceCalculationResult(result);

    } catch (error) {
        showError('Lỗi khi tính giá: ' + error.message);
    } finally {
        if (calcBtn) {
            calcBtn.textContent = '🧮 Tính Giá';
            calcBtn.disabled = false;
        }
    }
}

/**
 * Render bảng kết quả tính giá từng giờ ra giao diện.
 * Hiển thị màu sắc khác nhau cho Giờ Vàng vs Giờ Thường.
 * @param {Object} result PriceCalculationResponse từ API
 */
function renderPriceCalculationResult(result) {
    const container = document.getElementById('calcResult');
    if (!container) return;

    // Tạo HTML bảng chi tiết từng giờ
    const rowsHtml = result.hourlyDetails.map(detail => {
        // Giờ vàng: Nền vàng nhạt để nổi bật
        const isGolden = detail.multiplier > 1.0;
        const rowStyle = isGolden ? 'background:#fff9e6;' : '';
        return `
            <tr style="${rowStyle}">
                <td>${detail.hour}:00 – ${detail.hour + 1}:00</td>
                <td>${isGolden ? `⭐ ${detail.label}` : detail.label}</td>
                <td>×${detail.multiplier}</td>
                <td style="font-weight:bold; color:${isGolden ? '#e67e22' : '#27ae60'}">
                    ${formatCurrency(detail.price)}
                </td>
            </tr>
        `;
    }).join('');

    container.innerHTML = `
        <div class="calc-result-box">
            <h3>📊 Kết quả tính giá – ${result.fieldName}</h3>
            <p>Thời gian đặt: <strong>${result.startHour}:00 → ${result.endHour}:00</strong>
               (${result.totalHours} tiếng)</p>
            
            <table class="table-base">
                <thead>
                    <tr>
                        <th>Khung giờ</th>
                        <th>Loại giờ</th>
                        <th>Hệ số</th>
                        <th>Tiền giờ đó</th>
                    </tr>
                </thead>
                <tbody>${rowsHtml}</tbody>
            </table>

            <div class="total-price-box">
                💰 TỔNG TIỀN: <strong>${formatCurrency(result.totalPrice)}</strong>
            </div>
        </div>
    `;
}

// ============================================================
// PHẦN 8: QUẢN LÝ INPUTS GIỜ VÀNG TRONG FORM CHÍNH
// ============================================================

/**
 * Thêm 1 hàng input giờ vàng vào form tạo/sửa sân.
 * Admin có thể thêm nhiều khung giờ vàng khác nhau cho 1 sân.
 */
function addPriceConfigRow() {
    const container = document.getElementById('priceConfigContainer');
    if (!container) return;

    // Tạo 1 hàng input gồm: nhãn, giờ bắt đầu, giờ kết thúc, multiplier, nút xóa
    const rowId = 'price-row-' + Date.now(); // ID duy nhất cho mỗi hàng
    const row = document.createElement('div');
    row.className = 'price-config-row';
    row.id = rowId;
    row.innerHTML = `
        <input type="text"     placeholder="Nhãn (VD: Giờ Vàng)"  class="pc-label"      value="Giờ Vàng">
        <input type="number"   placeholder="Từ giờ (VD: 17)"       class="pc-start-hour" min="0" max="23" value="17">
        <input type="number"   placeholder="Đến giờ (VD: 21)"      class="pc-end-hour"   min="1" max="24" value="21">
        <input type="number"   placeholder="Hệ số (VD: 1.5)"       class="pc-multiplier" step="0.1" min="1" max="5" value="1.5">
        <button type="button"  class="btn btn-danger btn-sm"        onclick="removePriceConfigRow('${rowId}')">✕</button>
    `;
    container.appendChild(row);
}

/**
 * Xóa 1 hàng cấu hình giờ vàng khỏi form.
 * @param {string} rowId ID của hàng cần xóa
 */
function removePriceConfigRow(rowId) {
    const row = document.getElementById(rowId);
    if (row) row.remove();
}

/**
 * Thu thập toàn bộ dữ liệu giờ vàng từ các input trong form.
 * @returns {Array} Danh sách PriceConfigDTO để gửi lên API
 */
function collectPriceConfigInputs() {
    const rows = document.querySelectorAll('.price-config-row');
    const configs = [];
    rows.forEach(row => {
        const startHour  = parseInt(row.querySelector('.pc-start-hour').value);
        const endHour    = parseInt(row.querySelector('.pc-end-hour').value);
        const multiplier = parseFloat(row.querySelector('.pc-multiplier').value);
        const label      = row.querySelector('.pc-label').value.trim();

        // Chỉ thêm nếu dữ liệu hợp lệ
        if (!isNaN(startHour) && !isNaN(endHour) && !isNaN(multiplier) && endHour > startHour) {
            configs.push({ startHour, endHour, multiplier, label: label || 'Giờ Vàng' });
        }
    });
    return configs;
}

/**
 * Render các input giờ vàng từ dữ liệu có sẵn (dùng khi mở form sửa sân).
 * @param {Array} configs Danh sách PriceConfigDTO từ API
 */
function renderPriceConfigInputs(configs) {
    const container = document.getElementById('priceConfigContainer');
    if (!container) return;
    container.innerHTML = ''; // Xóa hết input cũ

    // Tạo lại từng hàng input từ dữ liệu
    configs.forEach(cfg => {
        const rowId = 'price-row-' + Date.now() + Math.random();
        const row = document.createElement('div');
        row.className = 'price-config-row';
        row.id = rowId;
        row.innerHTML = `
            <input type="text"   class="pc-label"      value="${cfg.label || 'Giờ Vàng'}">
            <input type="number" class="pc-start-hour" min="0" max="23" value="${cfg.startHour}">
            <input type="number" class="pc-end-hour"   min="1" max="24" value="${cfg.endHour}">
            <input type="number" class="pc-multiplier" step="0.1" min="1" max="5" value="${cfg.multiplier}">
            <button type="button" class="btn btn-danger btn-sm" onclick="removePriceConfigRow('${rowId}')">✕</button>
        `;
        container.appendChild(row);
    });
}

// ============================================================
// PHẦN 9: CÁC HÀM TIỆN ÍCH (HELPER FUNCTIONS)
// ============================================================

/** Định dạng số tiền theo kiểu Việt Nam (VD: 200.000 VND) */
function formatCurrency(amount) {
    if (amount === null || amount === undefined) return '—';
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency', currency: 'VND'
    }).format(amount);
}

/** Chuyển đổi loại sân từ "5"/"7"/"11" thành dạng dễ đọc */
function formatFieldType(type) {
    const typeMap = { '5': 'Sân 5 người', '7': 'Sân 7 người', '11': 'Sân 11 người' };
    return typeMap[type] || `Sân ${type} người`;
}

/** Chuyển đổi trạng thái từ enum sang tiếng Việt */
function formatStatus(status) {
    const statusMap = {
        'AVAILABLE':   '✅ Đang hoạt động',
        'MAINTENANCE': '🔧 Đang bảo trì'
    };
    return statusMap[status] || status;
}

/** Hiện/ẩn trạng thái loading trên bảng dữ liệu */
function showLoadingState(isLoading) {
    const tbody = document.getElementById('fieldTableBody');
    if (tbody && isLoading) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">
            ⏳ Đang tải dữ liệu...</td></tr>`;
    }
}

/** Hiển thị thông báo lỗi */
function showError(message) {
    const errorDiv = document.getElementById('fieldErrorMessage');
    if (errorDiv) {
        errorDiv.textContent = '❌ ' + message;
        errorDiv.style.display = 'block';
        // Tự động ẩn sau 5 giây
        setTimeout(() => { errorDiv.style.display = 'none'; }, 5000);
    } else {
        alert('Lỗi: ' + message);
    }
}

/** Hiển thị thông báo thành công */
function showSuccess(message) {
    const successDiv = document.getElementById('fieldSuccessMessage');
    if (successDiv) {
        successDiv.textContent = '✅ ' + message;
        successDiv.style.display = 'block';
        setTimeout(() => { successDiv.style.display = 'none'; }, 3000);
    } else {
        alert(message);
    }
}

/** Helper: lấy giá trị từ input theo ID */
function getFieldValue(id) {
    const el = document.getElementById(id);
    return el ? el.value : '';
}

/** Helper: đặt giá trị cho input theo ID */
function setFieldValue(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value !== null && value !== undefined ? value : '';
}

/** Reset toàn bộ các ô input trong form tạo sân về trạng thái trống */
function resetFieldForm() {
    setFieldValue('fieldName', '');
    setFieldValue('fieldType', '5');
    setFieldValue('fieldBasePrice', '');
    setFieldValue('fieldStatus', 'AVAILABLE');
    setFieldValue('fieldDescription', '');
    setFieldValue('fieldAddress', '');
    setFieldValue('fieldImageUrl', '');

    // Xóa hết các hàng cấu hình giờ vàng
    const container = document.getElementById('priceConfigContainer');
    if (container) container.innerHTML = '';
}

/** Hiển thị modal bằng cách thêm class 'active' */
function showModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.style.display = 'flex';
}

/** Ẩn modal */
function hideModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.style.display = 'none';
}

// ============================================================
// KHỞI CHẠY MODULE KHI TRANG ĐƯỢC TẢI
// ============================================================

/**
 * Tự động khởi tạo module khi DOM đã sẵn sàng.
 * Kiểm tra xem trang có chứa bảng danh sách sân (fieldTableBody) không
 * để tránh chạy nhầm trên các trang khác không liên quan.
 */
document.addEventListener('DOMContentLoaded', () => {
    // Chỉ khởi tạo nếu trang hiện tại có bảng danh sách sân
    if (document.getElementById('fieldTableBody')) {
        initFieldModule();
    }
});
