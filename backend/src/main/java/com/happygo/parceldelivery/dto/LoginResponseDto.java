package com.happygo.parceldelivery.dto;

public class LoginResponseDto {
    private boolean success;
    private String message;
    private String token;
    private UserInfo user;
    
    public LoginResponseDto() {}
    
    public LoginResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public LoginResponseDto(boolean success, String message, String token, UserInfo user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    // Inner class for user info
    public static class UserInfo {
        private Long id;
        private String phoneNumber;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String phoneNumber) {
            this.id = id;
            this.phoneNumber = phoneNumber;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getPhoneNumber() {
            return phoneNumber;
        }
        
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}

