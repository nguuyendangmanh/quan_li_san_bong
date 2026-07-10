package com.football.service;

import com.football.dto.OrderServiceRequest;
import com.football.dto.ServiceDTO;
import com.football.entity.BookingService;
import com.football.entity.Service;
import com.football.repository.BookingServiceRepository;
import com.football.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service xử lý nghiệp vụ quản lý kho và dịch vụ đi kèm.
 * Đảm bảo các thao tác trừ kho diễn ra an toàn đồng thời bằng Transaction.
 */
@org.springframework.stereotype.Service
public class InventoryService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private BookingServiceRepository bookingServiceRepository;

    /**
     * Lấy toàn bộ danh sách dịch vụ đang kinh doanh
     */
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Lấy thông tin chi tiết của 1 dịch vụ
     */
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ có mã: " + id));
    }

    /**
     * Tạo dịch vụ mới (Admin)
     */
    @Transactional
    public Service createService(ServiceDTO dto) {
        Service service = new Service();
        service.setName(dto.getName());
        service.setPrice(dto.getPrice());
        service.setStockQuantity(dto.getStockQuantity());
        return serviceRepository.save(service);
    }

    /**
     * Cập nhật thông tin dịch vụ và nhập thêm kho (Admin)
     */
    @Transactional
    public Service updateService(Long id, ServiceDTO dto) {
        Service service = getServiceById(id);
        service.setName(dto.getName());
        service.setPrice(dto.getPrice());
        service.setStockQuantity(dto.getStockQuantity());
        return serviceRepository.save(service);
    }

    /**
     * Xóa dịch vụ khỏi hệ thống
     */
    @Transactional
    public void deleteService(Long id) {
        Service service = getServiceById(id);
        serviceRepository.delete(service);
    }

    /**
     * Xử lý gọi đồ uống tại quầy cho khách và trừ tồn kho tự động.
     * - Sử dụng @Transactional để rollback toàn bộ nếu có bất kỳ sản phẩm nào không đủ kho.
     * - Trừ kho an toàn bằng câu lệnh Update nguyên tử của SQL.
     */
    @Transactional
    public void orderServiceForBooking(OrderServiceRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Danh sách dịch vụ gọi thêm không được để trống!");
        }

        for (OrderServiceRequest.Item item : request.getItems()) {
            Long serviceId = item.getServiceId();
            Integer quantity = item.getQuantity();

            if (quantity <= 0) {
                throw new RuntimeException("Số lượng gọi dịch vụ phải lớn hơn 0!");
            }

            // 1. Kiểm tra sự tồn tại của dịch vụ
            Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại!"));

            // 2. Thực hiện trừ kho trực tiếp bằng câu lệnh SQL nguyên tử (Atomic Update)
            int affectedRows = serviceRepository.decrementStock(serviceId, quantity);

            // 3. Nếu số lượng dòng bị ảnh hưởng là 0, nghĩa là tồn kho không đủ để trừ
            if (affectedRows == 0) {
                throw new RuntimeException("Sản phẩm '" + service.getName() + "' không đủ số lượng trong kho! (Hiện còn: " + service.getStockQuantity() + ")");
            }

            // 4. Lưu lịch sử gọi dịch vụ vào bảng booking_services
            BookingService bookingService = new BookingService();
            bookingService.setBookingId(request.getBookingId());
            bookingService.setService(service);
            bookingService.setQuantity(quantity);
            bookingService.setPrice(service.getPrice());
            
            // Thành tiền = Đơn giá dịch vụ * Số lượng gọi
            double subtotal = service.getPrice() * quantity;
            bookingService.setSubtotal(subtotal);

            bookingServiceRepository.save(bookingService);
        }
    }
}
