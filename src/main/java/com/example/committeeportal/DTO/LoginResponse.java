package com.example.committeeportal.DTO;

public class LoginResponse {
    private String token;
    private String email;
    private Long userId;
    private String userName;
    private String role;
    private String message;

    public LoginResponse() {}

    public LoginResponse(String token, String email, Long userId, String userName, String role) {
        this.token = token;
        this.email = email;
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.message = "Login successful";
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
