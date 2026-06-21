package com.football.entity;

import jakarta.persistence.*;

/**
 * Entity đại diện cho bảng 'booking_services' trong cơ sở dữ liệu.
 * Dùng để lưu trữ chi tiết các dịch vụ/nước uống đã đặt kèm cho từng lượt đặt sân.
 */
@Entity
@Table(name = "booking_services")
public class BookingService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chỉ lưu bookingId để tránh lỗi biên dịch khi TV3 chưa hoàn thành Entity Booking
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    // Thiết lập mối quan hệ ManyToOne tới Entity Service có sẵn của TV4
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double subtotal;

    // Constructor mặc định bắt buộc cho JPA
    public BookingService() {}

    // Constructor đầy đủ tham số
    public BookingService(Long id, Long bookingId, Service service, Integer quantity, Double subtotal) {
        this.id = id;
        this.bookingId = bookingId;
        this.service = service;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Các hàm Getter và Setter chuẩn
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
