package com.happygo.parceldelivery.controller;

import com.happygo.parceldelivery.dto.ParcelRequestDto;
import com.happygo.parceldelivery.entity.ParcelRequest;
import com.happygo.parceldelivery.service.ParcelRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parcel-requests")
@CrossOrigin(origins = "http://localhost:3000")
public class ParcelRequestController {
    
    @Autowired
    private ParcelRequestService parcelRequestService;
    
    @PostMapping
    public ResponseEntity<ParcelRequest> createParcelRequest(@Valid @RequestBody ParcelRequestDto dto) {
        ParcelRequest created = parcelRequestService.createParcelRequest(dto);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping
    public ResponseEntity<List<ParcelRequest>> getAllParcelRequests() {
        List<ParcelRequest> requests = parcelRequestService.getAllParcelRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<List<ParcelRequest>> getUnreadParcelRequests() {
        List<ParcelRequest> requests = parcelRequestService.getUnreadParcelRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Long count = parcelRequestService.getUnreadCount();
        return ResponseEntity.ok(count);
    }
    
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<ParcelRequest> markAsRead(@PathVariable Long id) {
        ParcelRequest request = parcelRequestService.markAsRead(id);
        return ResponseEntity.ok(request);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ParcelRequest> getParcelRequestById(@PathVariable Long id) {
        ParcelRequest request = parcelRequestService.getParcelRequestById(id);
        return ResponseEntity.ok(request);
    }
}
