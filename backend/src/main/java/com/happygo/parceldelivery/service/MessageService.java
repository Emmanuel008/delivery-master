package com.happygo.parceldelivery.service;

import com.happygo.parceldelivery.dto.MessageDto;
import com.happygo.parceldelivery.entity.Message;
import com.happygo.parceldelivery.entity.OutboxSms;
import com.happygo.parceldelivery.repository.MessageRepository;
import com.happygo.parceldelivery.repository.OutboxSmsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private OutboxSmsRepository outboxSmsRepository;
    
    @Value("${kilakona.api.url:API_URL}")
    private String apiUrl;
    
    @Value("${kilakona.api.key:API_KEY}")
    private String apiKey;
    
    @Value("${kilakona.api.secret:API_SECRET}")
    private String apiSecret;
    
    @Value("${kilakona.sender.id:SENDER_ID}")
    private String senderId;
    
    @Value("${kilakona.delivery.callback:DELIVERY_CALLBACK}")
    private String deliveryCallback;
    
    public Message createMessage(MessageDto dto) {
        Message message = new Message(dto.getContent(), dto.getPhoneNumber());
        return messageRepository.save(message);
    }
    
    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<OutboxSms> getAllSmsMessages() {
        return outboxSmsRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<Map<String, Object>> getAllMessagesCombined() {
        List<Map<String, Object>> combined = new ArrayList<>();
        
        // Get all draft messages from messages table
        List<Message> draftMessages = messageRepository.findAllByOrderByCreatedAtDesc();
        for (Message msg : draftMessages) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("id", msg.getId());
            messageMap.put("phoneNumber", msg.getPhoneNumber());
            messageMap.put("content", msg.getContent());
            messageMap.put("status", msg.getIsSent() ? "Sent" : "Draft");
            messageMap.put("createdAt", msg.getCreatedAt());
            messageMap.put("messageId", null);
            messageMap.put("response", null);
            combined.add(messageMap);
        }
        
        // Get all SMS messages from outbox_sms table  
        List<OutboxSms> smsMessages = outboxSmsRepository.findAllByOrderByCreatedAtDesc();
        for (OutboxSms sms : smsMessages) {
            Map<String, Object> smsMap = new HashMap<>();
            smsMap.put("id", sms.getId());
            smsMap.put("phoneNumber", sms.getPhoneNumber());
            smsMap.put("content", sms.getMessage());
            smsMap.put("status", sms.getStatus());
            smsMap.put("createdAt", sms.getCreatedAt());
            smsMap.put("messageId", sms.getMessageId());
            smsMap.put("response", sms.getResponse());
            combined.add(smsMap);
        }
        
        // Sort by createdAt descending
        combined.sort((a, b) -> {
            String dateA = a.get("createdAt").toString();
            String dateB = b.get("createdAt").toString();
            return dateB.compareTo(dateA);
        });
        
        return combined;
    }
    
    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found"));
    }
    
    public Message markAsSent(Long id) {
        Message message = messageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        message.setIsSent(true);
        return messageRepository.save(message);
    }

    private String safeSnippet(String s) {
        if (s == null) return "";
        String trimmed = s.replaceAll("\n", " ");
        return trimmed.length() > 500 ? trimmed.substring(0, 500) + "..." : trimmed;
    }

    public Map<String, Object> sendSms(MessageDto dto) {
        RestTemplate restTemplate = new RestTemplate();

        /* Set headers */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api_key", apiKey);
        headers.set("api_secret", apiSecret);

        /* Set payload */
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", senderId);
        payload.put("messageType", "text");
        payload.put("message", dto.getContent());
        
        // Since API expects without '+', remove + from phone number
        payload.put("contacts", dto.getPhoneNumber().replace("+", "")); 
        payload.put("deliveryReportUrl", deliveryCallback != null ? deliveryCallback : "");

        /* Log request info */
        logger.info("Sending SMS to Kilakona => contacts={}, senderId={}, apiUrl={}", 
            dto.getPhoneNumber(), senderId, apiUrl);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        Map<String, Object> result = new HashMap<>();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            result.put("statusCode", response.getStatusCode().value());
            result.put("body", response.getBody());

            logger.info("Kilakona SMS response: status={}, body={}", 
                response.getStatusCode(), response.getBody());

            // Parse response to extract messageId and save properly
            String messageId = null;
            String status = "Pending";
            
            if (response.getStatusCode().is2xxSuccessful()) {
                try {
                    // Parse JSON response to extract shootId
                    String responseBody = response.getBody();
                    if (responseBody != null && responseBody.contains("\"shootId\"")) {
                        // Simple extraction of shootId from JSON
                        int startIndex = responseBody.indexOf("\"shootId\":\"") + 11;
                        int endIndex = responseBody.indexOf("\"", startIndex);
                        if (startIndex > 10 && endIndex > startIndex) {
                            messageId = responseBody.substring(startIndex, endIndex);
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Failed to parse messageId from response: {}", e.getMessage());
                }
            } else {
                status = "Failed";
            }

            // Save outbox record with proper structure
            String fullResponse = String.format("Status: %d, Body: %s", 
                response.getStatusCode().value(), response.getBody());
            
            outboxSmsRepository.save(new OutboxSms(
                dto.getContent(), 
                dto.getPhoneNumber(), 
                status, 
                messageId, 
                fullResponse
            ));
        } 
        catch (RestClientResponseException ex) {
            result.put("statusCode", ex.getStatusCode().value());
            result.put("body", ex.getResponseBodyAsString());
            result.put("error", ex.getMessage());
            logger.error("Kilakona SMS request failed: status={}, body={}", 
                ex.getStatusCode(), ex.getResponseBodyAsString());

            // Save outbox record on provider error
            String fullErrorResponse = String.format("Status: %d, Body: %s, Error: %s", 
                ex.getStatusCode().value(), ex.getResponseBodyAsString(), ex.getMessage());
            
            outboxSmsRepository.save(new OutboxSms(
                dto.getContent(), 
                dto.getPhoneNumber(), 
                "Failed", 
                null, 
                fullErrorResponse
            ));
        } 
        catch (Exception ex) {
            result.put("statusCode", 0);
            result.put("error", ex.getMessage());
            logger.error("Kilakona SMS request error", ex);

            // Save outbox record on unexpected error
            String fullExceptionResponse = String.format("Status: 0, Error: %s", ex.getMessage());
            
            outboxSmsRepository.save(new OutboxSms(
                dto.getContent(), 
                dto.getPhoneNumber(), 
                "Error", 
                null, 
                fullExceptionResponse
            ));
        }

        return result;
    }
}


