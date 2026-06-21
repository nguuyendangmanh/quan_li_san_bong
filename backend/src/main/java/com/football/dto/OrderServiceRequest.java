package com.football.dto;

import java.util.List;

/**
 * DTO nhận yêu cầu đặt thêm đồ uống/dịch vụ từ Frontend tại quầy.
 */
public class OrderServiceRequest {
    private Long bookingId;
    private List<Item> items;

    public OrderServiceRequest() {}

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    /**
     * Lớp tĩnh đại diện cho từng loại đồ uống và số lượng được gọi.
     */
    public static class Item {
        private Long serviceId;
        private Integer quantity;

        public Item() {}

        public Long getServiceId() {
            return serviceId;
        }

        public void setServiceId(Long serviceId) {
            this.serviceId = serviceId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
