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

    @Column(name = "status", nullable = false, length = 255)
    private String status;

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

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
