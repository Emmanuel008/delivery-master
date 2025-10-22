package com.happygo.parceldelivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "parcel_requests")
public class ParcelRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;
    
    @NotNull(message = "Price is required")
    @Column(nullable = false)
    private Double price;
    
    @NotNull(message = "Delivery possible status is required")
    @Column(nullable = false)
    private Boolean canDeliver;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    // Constructors
    public ParcelRequest() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ParcelRequest(String name, String location, Double price, Boolean canDeliver, String phoneNumber) {
        this();
        this.name = name;
        this.location = location;
        this.price = price;
        this.canDeliver = canDeliver;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Boolean getCanDeliver() {
        return canDeliver;
    }
    
    public void setCanDeliver(Boolean canDeliver) {
        this.canDeliver = canDeliver;
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
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
