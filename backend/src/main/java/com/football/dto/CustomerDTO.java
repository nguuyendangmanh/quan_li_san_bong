package com.football.dto;

public class CustomerDTO {

    private Integer id;
    private String phoneNumber;
    private String fullName;
    private String role;
    private Integer loyaltyPoints;
    private String vipTier; // DONG, BAC, VANG, KIMCUONG

    public CustomerDTO() {}

    public CustomerDTO(Integer id, String phoneNumber, String fullName, String role, Integer loyaltyPoints) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.role = role;
        this.loyaltyPoints = loyaltyPoints;
        this.vipTier = resolveVipTier(loyaltyPoints);
    }

    private String resolveVipTier(Integer points) {
        if (points == null || points < 100) return "DONG";
        if (points < 300) return "BAC";
        if (points < 600) return "VANG";
        return "KIMCUONG";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        this.vipTier = resolveVipTier(loyaltyPoints);
    }

    public String getVipTier() { return vipTier; }
    public void setVipTier(String vipTier) { this.vipTier = vipTier; }
}
