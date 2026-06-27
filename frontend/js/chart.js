// ================================================================
// [TV5] chart.js -- Logic bieu do thong ke doanh thu
// Goi API tu ReportController, dung Chart.js 4.x de ve bieu do
// QUAN TRONG: Phu thuoc vao api-config.js (da load truoc trong HTML)
// ================================================================

// Bien luu cac instance Chart de huy truoc khi ve lai
let chartMonthly = null;
let chartFields  = null;
let chartDaily   = null;

// Nam dang xem (mac dinh nam hien tai)
let namHienTai = new Date().getFullYear();

// -- Khoi dong khi trang load xong --
document.addEventListener('DOMContentLoaded', () => {
    const inputNam = document.getElementById('filter-year');
    if (inputNam) inputNam.value = namHienTai;

    const btnFilter = document.getElementById('btn-filter-year');
    if (btnFilter) {
        btnFilter.addEventListener('click', () => {
            const val = parseInt(inputNam.value, 10);
            if (!val || val < 2020 || val > 2099) {
                alert('Vui long nhap nam hop le (2020 - 2099)');
                return;
            }
            namHienTai = val;
            taiToanBo();
        });
    }

    taiToanBo();
});

// -- Tai tat ca du lieu cung luc --
async function taiToanBo() {
    await Promise.all([
        taiDashboardStats(),
        taiBieuDoCot(),
        taiPieChart()
    ]);
}

// -- Dashboard: 6 the so lieu nhanh --
async function taiDashboardStats() {
    try {
        const stats = await fetchAPI('/reports/dashboard');
        if (!stats) return;

        const formatTien = (so) => {
            const num = parseFloat(so) || 0;
            return num.toLocaleString('vi-VN') + ' d';
        };

        setText('stat-revenue-today',   formatTien(stats.doanhThuHomNay));
        setText('stat-revenue-month',   formatTien(stats.doanhThuThangNay));
        setText('stat-bookings-today',  stats.tongLuotDatHomNay ?? '--');
        setText('stat-bookings-month',  stats.tongLuotDatThangNay ?? '--');
        setText('stat-customers',       stats.tongKhachHang ?? '--');
        setText('stat-fields-active',   stats.sanDangHoatDong ?? '--');
    } catch (err) {
        console.error('Loi tai dashboard stats:', err);
    }
}

// -- Bieu do cot: Doanh thu 12 thang --
async function taiBieuDoCot() {
    setError('chart-monthly-error', '');
    try {
        const data = await fetchAPI('/reports/revenue/monthly?nam=' + namHienTai);
        if (!data) return;

        if (!data.length) {
            setError('chart-monthly-error', 'Chua co du lieu doanh thu cho nam ' + namHienTai);
            return;
        }

        const labelsFull = Array.from({ length: 12 }, (_, i) => 'T' + (i + 1));
        const mapSan = {};
        const mapDV  = {};

        data.forEach(row => {
            const thangSo = parseInt(row.thoiGian.split('-')[1], 10);
            const key = 'T' + thangSo;
            mapSan[key] = parseFloat(row.doanhThuSan)   || 0;
            mapDV[key]  = parseFloat(row.doanhThuDichVu) || 0;
        });

        const dataSan = labelsFull.map(k => mapSan[k] || 0);
        const dataDV  = labelsFull.map(k => mapDV[k]  || 0);

        if (chartMonthly) chartMonthly.destroy();

        const ctx = document.getElementById('chart-monthly').getContext('2d');
        chartMonthly = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labelsFull,
                datasets: [
                    {
                        label: 'Doanh thu san',
                        data: dataSan,
                        backgroundColor: 'rgba(54, 162, 235, 0.7)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    },
                    {
                        label: 'Doanh thu dich vu',
                        data: dataDV,
                        backgroundColor: 'rgba(255, 159, 64, 0.7)',
                        borderColor: 'rgba(255, 159, 64, 1)',
                        borderWidth: 1
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return ctx.dataset.label + ': ' + ctx.parsed.y.toLocaleString('vi-VN') + ' d';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(val) {
                                return (val / 1000000).toFixed(0) + ' tr';
                            }
                        }
                    }
                },
                onClick: function(event, elements) {
                    if (elements.length > 0) {
                        const thangIndex = elements[0].index + 1;
                        taiLineChart(namHienTai, thangIndex);
                    }
                }
            }
        });
    } catch (err) {
        setError('chart-monthly-error', 'Loi tai du lieu theo thang: ' + err.message);
    }
}

// -- Bieu do tron: Ti le doanh thu theo san --
async function taiPieChart() {
    setError('chart-fields-error', '');
    try {
        const data = await fetchAPI('/reports/revenue/fields?nam=' + namHienTai);
        if (!data) return;

        if (!data.length) {
            setError('chart-fields-error', 'Chua co du lieu theo san cho nam ' + namHienTai);
            return;
        }

        const labels   = data.map(function(r) { return r.tenSan; });
        const doanhThu = data.map(function(r) { return parseFloat(r.doanhThu) || 0; });

        const mauNen = [
            '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
            '#9966FF', '#FF9F40', '#C9CBCF', '#7BC8A4'
        ];

        if (chartFields) chartFields.destroy();

        const ctx = document.getElementById('chart-fields').getContext('2d');
        chartFields = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: doanhThu,
                    backgroundColor: mauNen.slice(0, labels.length),
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'right' },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                const tong = ctx.dataset.data.reduce(function(a, b) { return a + b; }, 0);
                                const phan = tong > 0 ? ((ctx.parsed / tong) * 100).toFixed(1) : 0;
                                return ctx.label + ': ' + ctx.parsed.toLocaleString('vi-VN') + ' d (' + phan + '%)';
                            }
                        }
                    }
                }
            }
        });
    } catch (err) {
        setError('chart-fields-error', 'Loi tai du lieu theo san: ' + err.message);
    }
}

// -- Bieu do duong: Doanh thu tung ngay trong thang --
async function taiLineChart(nam, thang) {
    setError('chart-daily-error', '');

    document.getElementById('section-daily').scrollIntoView({ behavior: 'smooth' });

    try {
        const data = await fetchAPI('/reports/revenue/daily?nam=' + nam + '&thang=' + thang);
        if (!data) return;

        if (!data.length) {
            setError('chart-daily-error', 'Thang ' + thang + '/' + nam + ' chua co doanh thu.');
            return;
        }

        const labels   = data.map(function(r) { return r.thoiGian.split('-')[2]; });
        const dataSan  = data.map(function(r) { return parseFloat(r.doanhThuSan)    || 0; });
        const dataDV   = data.map(function(r) { return parseFloat(r.doanhThuDichVu) || 0; });
        const dataTong = data.map(function(r) { return parseFloat(r.tongDoanhThu)   || 0; });

        if (chartDaily) chartDaily.destroy();

        const ctx = document.getElementById('chart-daily').getContext('2d');
        chartDaily = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Tong doanh thu',
                        data: dataTong,
                        borderColor: '#36A2EB',
                        backgroundColor: 'rgba(54,162,235,0.1)',
                        fill: true,
                        tension: 0.3,
                        pointRadius: 4
                    },
                    {
                        label: 'Doanh thu san',
                        data: dataSan,
                        borderColor: '#FF6384',
                        borderDash: [5, 5],
                        tension: 0.3,
                        pointRadius: 3,
                        fill: false
                    },
                    {
                        label: 'Doanh thu dich vu',
                        data: dataDV,
                        borderColor: '#FF9F40',
                        borderDash: [5, 5],
                        tension: 0.3,
                        pointRadius: 3,
                        fill: false
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: 'top' },
                    title: {
                        display: true,
                        text: 'Chi tiet doanh thu thang ' + thang + '/' + nam
                    },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return ctx.dataset.label + ': ' + ctx.parsed.y.toLocaleString('vi-VN') + ' d';
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(val) {
                                return (val / 1000000).toFixed(1) + ' tr';
                            }
                        }
                    }
                }
            }
        });
    } catch (err) {
        setError('chart-daily-error', 'Loi tai du lieu theo ngay: ' + err.message);
    }
}

// -- Helpers --
function setText(id, value) {
    var el = document.getElementById(id);
    if (el) el.textContent = value;
}

function setError(id, msg) {
    var el = document.getElementById(id);
    if (el) el.textContent = msg;
}
