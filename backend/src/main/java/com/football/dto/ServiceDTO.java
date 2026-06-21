package com.football.dto;

/**
 * DTO đại diện cho thông tin Dịch vụ gửi đi/nhận về từ Frontend.
 */
public class ServiceDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer stockQuantity;

    public ServiceDTO() {}

    public ServiceDTO(Long id, String name, Double price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
