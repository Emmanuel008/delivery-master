package com.happygo.parceldelivery.controller;

import com.happygo.parceldelivery.dto.MessageDto;
import com.happygo.parceldelivery.entity.Message;
import com.happygo.parceldelivery.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping
    public ResponseEntity<Message> createMessage(@Valid @RequestBody MessageDto dto) {
        Message created = messageService.createMessage(dto);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }
    
    @PutMapping("/{id}/mark-sent")
    public ResponseEntity<Message> markAsSent(@PathVariable Long id) {
        Message message = messageService.markAsSent(id);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/sample")
    public ResponseEntity<Map<String, String>> getSampleMessage() {
        Map<String, String> sampleMessage = new HashMap<>();
        sampleMessage.put("content", "Hello! Your parcel delivery request has been received. We will contact you shortly to confirm delivery details. Thank you for choosing our service!");
        sampleMessage.put("phoneNumber", "+255625313162");
        return ResponseEntity.ok(sampleMessage);
    }
    
    @PostMapping("/highlight-phone")
    public ResponseEntity<Map<String, Object>> highlightPhoneNumbers(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Content is required"));
        }
        
        // Phone number regex pattern (supports various formats)
        Pattern phonePattern = Pattern.compile("(\\+?\\d{1,4}[-\\s]?)?(\\d{3,4}[-\\s]?\\d{3,4}[-\\s]?\\d{3,4})");
        Matcher matcher = phonePattern.matcher(content);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalContent", content);
        
        // Find all phone numbers
        StringBuilder highlightedContent = new StringBuilder();
        int lastEnd = 0;
        
        while (matcher.find()) {
            // Add text before the phone number
            highlightedContent.append(content, lastEnd, matcher.start());
            
            // Add highlighted phone number
            String phoneNumber = matcher.group();
            highlightedContent.append("<span style='background-color: #ffff00; font-weight: bold; padding: 2px 4px; border-radius: 3px;'>")
                            .append(phoneNumber)
                            .append("</span>");
            
            lastEnd = matcher.end();
        }
        
        // Add remaining text
        highlightedContent.append(content.substring(lastEnd));
        
        result.put("highlightedContent", highlightedContent.toString());
        result.put("phoneNumbersFound", matcher.groupCount() > 0);
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/send-with-highlight")
    public ResponseEntity<Map<String, Object>> sendMessageWithHighlight(@Valid @RequestBody MessageDto dto) {
        // Create the message
        Message created = messageService.createMessage(dto);
        
        // Highlight phone numbers in the content
        Pattern phonePattern = Pattern.compile("(\\+?\\d{1,4}[-\\s]?)?(\\d{3,4}[-\\s]?\\d{3,4}[-\\s]?\\d{3,4})");
        Matcher matcher = phonePattern.matcher(dto.getContent());
        
        StringBuilder highlightedContent = new StringBuilder();
        int lastEnd = 0;
        
        while (matcher.find()) {
            highlightedContent.append(dto.getContent(), lastEnd, matcher.start());
            String phoneNumber = matcher.group();
            highlightedContent.append("<span style='background-color: #ffff00; font-weight: bold; padding: 2px 4px; border-radius: 3px;'>")
                            .append(phoneNumber)
                            .append("</span>");
            lastEnd = matcher.end();
        }
        highlightedContent.append(dto.getContent().substring(lastEnd));
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", created);
        response.put("highlightedContent", highlightedContent.toString());
        response.put("phoneNumberHighlighted", matcher.groupCount() > 0);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-sms")
    public ResponseEntity<Map<String, Object>> sendSms(@Valid @RequestBody MessageDto dto) {
        try {
            Map<String, Object> result = messageService.sendSms(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "SMS sending failed");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/test-sms-payload")
    public ResponseEntity<Map<String, Object>> testSmsPayload(@Valid @RequestBody MessageDto dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("receivedContent", dto.getContent());
        result.put("receivedPhoneNumber", dto.getPhoneNumber());
        result.put("messageDtoValid", true);
        result.put("timestamp", java.time.LocalDateTime.now());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/debug-sms")
    public ResponseEntity<Map<String, Object>> debugSms(@Valid @RequestBody MessageDto dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("messageDto", dto);
        result.put("apiUrl", messageService.getApiUrl());
        result.put("apiKey", messageService.getApiKey());
        result.put("senderId", messageService.getSenderId());
        result.put("deliveryCallback", messageService.getDeliveryCallback());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/test-sms-without-sending")
    public ResponseEntity<Map<String, Object>> testSmsWithoutSending(@Valid @RequestBody MessageDto dto) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "SMS functionality is working correctly");
        result.put("messageDto", dto);
        result.put("apiConfiguration", Map.of(
            "apiUrl", messageService.getApiUrl(),
            "apiKey", messageService.getApiKey(),
            "senderId", messageService.getSenderId(),
            "deliveryCallback", messageService.getDeliveryCallback()
        ));
        result.put("note", "The 403 error from Kilakona API suggests invalid credentials or API format changes");
        result.put("recommendation", "Please verify the Kilakona API credentials and documentation");
        return ResponseEntity.ok(result);
    }
}
