package com.happygo.parceldelivery.service;

import com.happygo.parceldelivery.dto.ParcelRequestDto;
import com.happygo.parceldelivery.entity.ParcelRequest;
import com.happygo.parceldelivery.repository.ParcelRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParcelRequestService {
    
    @Autowired
    private ParcelRequestRepository parcelRequestRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public ParcelRequest createParcelRequest(ParcelRequestDto dto) {
        ParcelRequest parcelRequest = new ParcelRequest(
            dto.getName(),
            dto.getLocation(),
            dto.getPrice(),
            dto.getCanDeliver(),
            dto.getPhoneNumber()
        );
        
        ParcelRequest savedRequest = parcelRequestRepository.save(parcelRequest);
        
        // Send real-time notification to admin dashboard
        messagingTemplate.convertAndSend("/topic/notifications", savedRequest);
        
        return savedRequest;
    }
    
    public List<ParcelRequest> getAllParcelRequests() {
        return parcelRequestRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<ParcelRequest> getUnreadParcelRequests() {
        return parcelRequestRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }
    
    public Long getUnreadCount() {
        return parcelRequestRepository.countUnreadRequests();
    }
    
    public ParcelRequest markAsRead(Long id) {
        ParcelRequest request = parcelRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parcel request not found"));
        
        request.setIsRead(true);
        return parcelRequestRepository.save(request);
    }
    
    public ParcelRequest getParcelRequestById(Long id) {
        return parcelRequestRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Parcel request not found"));
    }
}
