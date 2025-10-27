package com.happygo.parceldelivery.controller;

import com.happygo.parceldelivery.entity.OutboxSms;
import com.happygo.parceldelivery.repository.OutboxSmsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sms-callback")
@CrossOrigin(origins = "*")
public class SmsCallbackController {

    private static final Logger logger = LoggerFactory.getLogger(SmsCallbackController.class);

    @Autowired
    private OutboxSmsRepository outboxSmsRepository;

    @PostMapping("/status-update")
    public ResponseEntity<Map<String, Object>> updateSmsStatus(@RequestBody Map<String, Object> callbackData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            logger.info("Received SMS callback: {}", callbackData);
            
            // Extract messageId and status from callback
            String messageId = (String) callbackData.get("messageId");
            String status = (String) callbackData.get("status");
            
            if (messageId == null || status == null) {
                result.put("success", false);
                result.put("error", "Missing messageId or status in callback data");
                return ResponseEntity.badRequest().body(result);
            }
            
            // Find the SMS record by messageId
            Optional<OutboxSms> smsRecord = outboxSmsRepository.findByMessageId(messageId);
            
            if (smsRecord.isPresent()) {
                OutboxSms sms = smsRecord.get();
                sms.setStatus(status);
                outboxSmsRepository.save(sms);
                
                logger.info("Updated SMS status for messageId {} to {}", messageId, status);
                
                result.put("success", true);
                result.put("message", "SMS status updated successfully");
                result.put("messageId", messageId);
                result.put("newStatus", status);
                
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("error", "SMS record not found for messageId: " + messageId);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error processing SMS callback", e);
            result.put("success", false);
            result.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @GetMapping("/test/{messageId}")
    public ResponseEntity<Map<String, Object>> testStatusUpdate(@PathVariable String messageId, 
                                                               @RequestParam(defaultValue = "Delivered") String status) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Find the SMS record by messageId
            Optional<OutboxSms> smsRecord = outboxSmsRepository.findByMessageId(messageId);
            
            if (smsRecord.isPresent()) {
                OutboxSms sms = smsRecord.get();
                sms.setStatus(status);
                outboxSmsRepository.save(sms);
                
                logger.info("Test: Updated SMS status for messageId {} to {}", messageId, status);
                
                result.put("success", true);
                result.put("message", "SMS status updated successfully");
                result.put("messageId", messageId);
                result.put("newStatus", status);
                result.put("previousStatus", smsRecord.get().getStatus());
                
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("error", "SMS record not found for messageId: " + messageId);
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error in test status update", e);
            result.put("success", false);
            result.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
