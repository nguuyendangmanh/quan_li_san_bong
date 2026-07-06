package com.football.service;

import com.football.dto.*;
import com.football.entity.Field;
import com.football.entity.PriceConfig;
import com.football.repository.FieldRepository;
import com.football.repository.PriceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ==========================================
 * SERVICE: QUẢN LÝ SÂN BÓNG (FIELD SERVICE)
 * ==========================================
 * Chứa toàn bộ logic nghiệp vụ của module Sân bóng:
 * 1. CRUD cơ bản: Tạo / Xem / Sửa / Xóa sân
 * 2. Quản lý cấu hình giờ vàng (PriceConfig)
 * 3. Thuật toán tính giá đa hình theo khung giờ (Dynamic Pricing)
 */
@Service
public class FieldService {

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private PriceConfigRepository priceConfigRepository;

    // ============================================================
    // PHẦN 1: CÁC HÀM CRUD SÂN BÓNG
    // ============================================================

    /**
     * Lấy danh sách tất cả sân bóng trong hệ thống.
     * Mỗi sân sẽ kèm theo danh sách PriceConfig (giờ vàng) của nó.
     * @return Danh sách FieldResponseDTO để trả về cho Frontend
     */
    public List<FieldResponseDTO> getAllFields() {
        // Lấy toàn bộ sân từ DB
        List<Field> fields = fieldRepository.findAll();
        // Chuyển đổi (convert) từ Entity → DTO để trả về cho Frontend
        return fields.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy thông tin chi tiết của 1 sân cụ thể theo ID.
     * @param id ID của sân cần tìm
     * @return FieldResponseDTO kèm danh sách giờ vàng
     * @throws RuntimeException nếu không tìm thấy sân
     */
    public FieldResponseDTO getFieldById(Long id) {
        // orElseThrow: Nếu không tìm thấy ID → ném lỗi, Controller bắt và trả về 404
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bóng với ID: " + id));
        return convertToResponseDTO(field);
    }

    /**
     * Tạo sân bóng mới vào hệ thống.
     * Nếu có kèm PriceConfig → lưu luôn danh sách giờ vàng.
     * @param dto Dữ liệu sân từ Frontend gửi lên
     * @return FieldResponseDTO của sân vừa được tạo
     */
    @Transactional // Đảm bảo cả Field và PriceConfig được lưu trong cùng 1 transaction DB
    public FieldResponseDTO createField(FieldRequestDTO dto) {
        // Kiểm tra trùng tên sân để tránh nhầm lẫn
        if (fieldRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Tên sân '" + dto.getName() + "' đã tồn tại trong hệ thống!");
        }

        // Tạo Entity Field mới từ dữ liệu DTO
        Field field = new Field();
        field.setName(dto.getName());
        field.setType(dto.getType());
        field.setBasePricePerHour(dto.getBasePricePerHour());
        field.setStatus(dto.getStatus() != null ? dto.getStatus() : "AVAILABLE");
        field.setDescription(dto.getDescription());
        field.setImageUrl(dto.getImageUrl());
        field.setAddress(dto.getAddress());

        // Lưu sân vào DB trước để lấy được ID (cần ID để lưu PriceConfig)
        Field savedField = fieldRepository.save(field);

        // Nếu có cấu hình giờ vàng gửi kèm, lưu luôn
        if (dto.getPriceConfigs() != null && !dto.getPriceConfigs().isEmpty()) {
            savePriceConfigs(savedField, dto.getPriceConfigs());
        }

        return convertToResponseDTO(savedField);
    }

    /**
     * Cập nhật thông tin sân bóng đã tồn tại.
     * Đồng thời cập nhật lại toàn bộ cấu hình giờ vàng (xóa cũ, tạo mới).
     * @param id ID của sân cần cập nhật
     * @param dto Dữ liệu mới từ Frontend
     * @return FieldResponseDTO sau khi cập nhật
     */
    @Transactional
    public FieldResponseDTO updateField(Long id, FieldRequestDTO dto) {
        // Tìm sân cần sửa, ném lỗi nếu không có
        Field field = fieldRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bóng với ID: " + id));

        // Cập nhật từng trường thông tin của sân
        field.setName(dto.getName());
        field.setType(dto.getType());
        field.setBasePricePerHour(dto.getBasePricePerHour());
        if (dto.getStatus() != null) {
            field.setStatus(dto.getStatus());
        }
        field.setDescription(dto.getDescription());
        field.setImageUrl(dto.getImageUrl());
        field.setAddress(dto.getAddress());

        // Lưu thay đổi vào DB
        Field updatedField = fieldRepository.save(field);

        // Cập nhật PriceConfig: Chiến lược "Xóa hết → Tạo lại" để tránh xung đột
        priceConfigRepository.deleteByFieldId(id);
        if (dto.getPriceConfigs() != null && !dto.getPriceConfigs().isEmpty()) {
            savePriceConfigs(updatedField, dto.getPriceConfigs());
        }

        return convertToResponseDTO(updatedField);
    }

    /**
     * Xóa sân bóng khỏi hệ thống.
     * Nhờ CascadeType.ALL trên Field entity, các PriceConfig liên quan cũng bị xóa tự động.
     * @param id ID của sân cần xóa
     */
    public void deleteField(Long id) {
        // Kiểm tra sân có tồn tại không trước khi xóa
        if (!fieldRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sân bóng với ID: " + id);
        }
        fieldRepository.deleteById(id);
    }

    // ============================================================
    // PHẦN 2: QUẢN LÝ CẤU HÌNH GIỜ VÀNG (PRICE CONFIG)
    // ============================================================

    /**
     * Lấy danh sách toàn bộ cấu hình giờ vàng của 1 sân.
     * @param fieldId ID của sân cần xem cấu hình giá
     * @return Danh sách PriceConfigDTO
     */
    public List<PriceConfigDTO> getPriceConfigsByFieldId(Long fieldId) {
        // Kiểm tra sân có tồn tại không
        if (!fieldRepository.existsById(fieldId)) {
            throw new RuntimeException("Không tìm thấy sân bóng với ID: " + fieldId);
        }
        // Lấy danh sách PriceConfig từ DB và convert sang DTO
        return priceConfigRepository.findByFieldId(fieldId)
                .stream()
                .map(pc -> new PriceConfigDTO(
                        pc.getId(), pc.getStartHour(), pc.getEndHour(),
                        pc.getMultiplier(), pc.getLabel()))
                .collect(Collectors.toList());
    }

    /**
     * Lưu danh sách PriceConfig cho 1 sân (dùng nội bộ).
     * Hàm private, chỉ gọi từ createField() và updateField().
     * @param field Entity sân bóng đã được lưu vào DB
     * @param configs Danh sách cấu hình giờ vàng từ DTO
     */
    private void savePriceConfigs(Field field, List<PriceConfigDTO> configs) {
        for (PriceConfigDTO configDTO : configs) {
            PriceConfig config = new PriceConfig();
            config.setField(field);             // Liên kết với sân
            config.setStartHour(configDTO.getStartHour());
            config.setEndHour(configDTO.getEndHour());
            config.setMultiplier(configDTO.getMultiplier());
            config.setLabel(configDTO.getLabel());
            priceConfigRepository.save(config);
        }
    }

    // ============================================================
    // PHẦN 3: THUẬT TOÁN TÍNH GIÁ ĐA HÌNH (DYNAMIC PRICING)
    // ============================================================

    /**
     * ★ THUẬT TOÁN TÍNH GIÁ ĐA HÌNH THEO GIỜ VÀNG ★
     *
     * Ý tưởng thuật toán:
     * - Duyệt qua TỪNG GIỜ trong khoảng thời gian đặt sân [startHour, endHour)
     * - Với mỗi giờ: kiểm tra xem giờ đó có nằm trong khung "giờ vàng" nào không
     * - Nếu CÓ: nhân giá gốc với multiplier (hệ số tăng giá)
     * - Nếu KHÔNG: dùng đúng giá gốc (multiplier = 1.0)
     * - Cộng dồn giá của tất cả các giờ lại → ra tổng tiền phải trả
     *
     * Ví dụ minh họa:
     *   Sân A: basePricePerHour = 200,000 VND
     *   PriceConfig: 17:00 – 21:00, multiplier = 1.5 (Giờ Vàng)
     *   Đặt sân: 16:00 – 19:00 (3 tiếng)
     *     → 16:00–17:00: 200,000 × 1.0 = 200,000 (Giờ Thường)
     *     → 17:00–18:00: 200,000 × 1.5 = 300,000 (Giờ Vàng)
     *     → 18:00–19:00: 200,000 × 1.5 = 300,000 (Giờ Vàng)
     *     TỔNG = 800,000 VND
     *
     * @param request Chứa fieldId, startHour, endHour
     * @return PriceCalculationResponse với tổng tiền + chi tiết từng giờ
     */
    public PriceCalculationResponse calculatePrice(PriceCalculationRequest request) {
        // Bước 1: Tìm thông tin sân bóng và validate đầu vào
        Field field = fieldRepository.findById(request.getFieldId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sân bóng với ID: " + request.getFieldId()));

        int startHour = request.getStartHour();
        int endHour = request.getEndHour();

        // Validate: giờ kết thúc phải sau giờ bắt đầu
        if (endHour <= startHour) {
            throw new RuntimeException("Giờ kết thúc phải sau giờ bắt đầu!");
        }
        // Validate: giờ đặt phải nằm trong khoảng hợp lệ (0–24)
        if (startHour < 0 || endHour > 24) {
            throw new RuntimeException("Giờ đặt sân không hợp lệ (phải trong khoảng 0 – 24 giờ)!");
        }

        // Bước 2: Lấy toàn bộ cấu hình giờ vàng của sân này từ DB
        List<PriceConfig> priceConfigs = priceConfigRepository.findByFieldId(field.getId());

        // Bước 3: Duyệt từng giờ trong khoảng đặt và tính giá
        double totalPrice = 0.0;
        List<PriceCalculationResponse.HourlyDetail> hourlyDetails = new ArrayList<>();

        for (int hour = startHour; hour < endHour; hour++) {
            // Tìm xem giờ hiện tại (hour) có nằm trong khung giờ vàng nào không
            PriceConfig matchedConfig = findMatchingPriceConfig(priceConfigs, hour);

            double multiplier;
            String label;

            if (matchedConfig != null) {
                // Giờ này nằm trong khung giờ vàng → áp hệ số tăng giá
                multiplier = matchedConfig.getMultiplier();
                label = matchedConfig.getLabel() != null ? matchedConfig.getLabel() : "Giờ Vàng";
            } else {
                // Giờ thường → hệ số nhân = 1.0 (không tăng giá)
                multiplier = 1.0;
                label = "Giờ Thường";
            }

            // Tính giá cho giờ này = giá gốc × hệ số nhân
            double hourlyPrice = field.getBasePricePerHour() * multiplier;
            totalPrice += hourlyPrice;

            // Lưu lại chi tiết để trả về cho Frontend hiển thị bảng breakdown
            hourlyDetails.add(new PriceCalculationResponse.HourlyDetail(
                    hour, hourlyPrice, multiplier, label
            ));
        }

        // Bước 4: Đóng gói kết quả vào Response DTO
        PriceCalculationResponse response = new PriceCalculationResponse();
        response.setFieldName(field.getName());
        response.setStartHour(startHour);
        response.setEndHour(endHour);
        response.setTotalHours(endHour - startHour);
        response.setTotalPrice(totalPrice);
        response.setHourlyDetails(hourlyDetails);

        return response;
    }

    /**
     * Hàm trợ giúp: Tìm PriceConfig phù hợp với 1 giờ cụ thể.
     * Logic: Giờ 'hour' nằm trong khung [startHour, endHour) của PriceConfig nào thì trả về cái đó.
     * Nếu có nhiều PriceConfig trùng giờ → ưu tiên cái có multiplier CAO NHẤT (có lợi cho doanh thu).
     *
     * @param configs Danh sách PriceConfig của sân
     * @param hour    Giờ cần kiểm tra (VD: 17)
     * @return PriceConfig phù hợp, hoặc null nếu là giờ thường
     */
    private PriceConfig findMatchingPriceConfig(List<PriceConfig> configs, int hour) {
        PriceConfig bestMatch = null;
        for (PriceConfig config : configs) {
            // Kiểm tra giờ 'hour' có nằm trong khung [startHour, endHour) không
            if (hour >= config.getStartHour() && hour < config.getEndHour()) {
                // Nếu có nhiều config trùng, chọn cái có multiplier cao nhất
                if (bestMatch == null || config.getMultiplier() > bestMatch.getMultiplier()) {
                    bestMatch = config;
                }
            }
        }
        return bestMatch;
    }

    // ============================================================
    // PHẦN 4: HÀM CHUYỂN ĐỔI ENTITY → DTO (INTERNAL HELPER)
    // ============================================================

    /**
     * Chuyển đổi Entity Field thành FieldResponseDTO để trả về cho Frontend.
     * Quy tắc dự án: KHÔNG bao giờ trả Entity thẳng ra ngoài, phải qua DTO.
     * @param field Entity cần convert
     * @return FieldResponseDTO đã được điền đầy đủ thông tin
     */
    private FieldResponseDTO convertToResponseDTO(Field field) {
        // Lấy danh sách PriceConfig từ DB (không dùng lazy load của JPA để tránh N+1 problem)
        List<PriceConfigDTO> configDTOs = priceConfigRepository.findByFieldId(field.getId())
                .stream()
                .map(pc -> new PriceConfigDTO(
                        pc.getId(), pc.getStartHour(), pc.getEndHour(),
                        pc.getMultiplier(), pc.getLabel()))
                .collect(Collectors.toList());

        return new FieldResponseDTO(
                field.getId(),
                field.getName(),
                field.getType(),
                field.getBasePricePerHour(),
                field.getStatus(),
                field.getDescription(),
                field.getImageUrl(),
                field.getAddress(),
                configDTOs
        );
    }
}
