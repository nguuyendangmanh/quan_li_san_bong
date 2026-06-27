package com.football.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "field_type", nullable = false)
    private Integer fieldType;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(length = 20)
    private String status;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getFieldType() { return fieldType; }
    public void setFieldType(Integer fieldType) { this.fieldType = fieldType; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
