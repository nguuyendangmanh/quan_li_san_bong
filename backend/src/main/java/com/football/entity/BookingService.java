package com.football.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_services")
public class BookingService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @Column(name = "service_id", nullable = false)
    private Integer serviceId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    // Tổng tiền = đơn giá * số lượng — query báo cáo dùng cột này
    @Column(nullable = false)
    private BigDecimal subtotal;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
