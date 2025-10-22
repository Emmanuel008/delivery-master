package com.happygo.parceldelivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Message content is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_sent")
    private Boolean isSent = false;
    
    // Constructors
    public Message() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Message(String content, String phoneNumber) {
        this();
        this.content = content;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsSent() {
        return isSent;
    }
    
    public void setIsSent(Boolean isSent) {
        this.isSent = isSent;
    }
}
