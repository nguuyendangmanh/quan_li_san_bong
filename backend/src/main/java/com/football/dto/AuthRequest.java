package com.football.dto;

public class AuthRequest {
    private String phone;
    private String password;

    public AuthRequest() {}

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
