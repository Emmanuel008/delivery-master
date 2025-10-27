package com.happygo.parceldelivery.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_sms")
public class OutboxSms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "phone_number", nullable = false, length = 32)
    private String phoneNumber;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "message_id", length = 100)
    private String messageId;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public OutboxSms() {}

    public OutboxSms(String message, String phoneNumber, String status) {
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public OutboxSms(String message, String phoneNumber, String status, String messageId, String response) {
        this.message = message;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.messageId = messageId;
        this.response = response;
    }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
