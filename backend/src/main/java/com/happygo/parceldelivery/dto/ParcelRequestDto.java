package com.happygo.parceldelivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ParcelRequestDto {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Price is required")
    private Double price;
    
    @NotNull(message = "Delivery possible status is required")
    private Boolean canDeliver;
    
    private String phoneNumber;
    
    // Constructors
    public ParcelRequestDto() {}
    
    public ParcelRequestDto(String name, String location, Double price, Boolean canDeliver, String phoneNumber) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.canDeliver = canDeliver;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
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
}
