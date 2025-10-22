package com.happygo.parceldelivery.dto;

import jakarta.validation.constraints.NotBlank;

public class MessageDto {
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    // Constructors
    public MessageDto() {}
    
    public MessageDto(String content, String phoneNumber) {
        this.content = content;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
