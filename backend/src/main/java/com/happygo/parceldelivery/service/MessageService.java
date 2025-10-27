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
            outboxSmsRepository.save(new OutboxSms(
                dto.getContent(), 
                dto.getPhoneNumber(), 
                status, 
                messageId, 
                response.getBody()
            ));
        } 
        catch (RestClientResponseException ex) {
            result.put("statusCode", ex.getStatusCode().value());
            result.put("body", ex.getResponseBodyAsString());
            result.put("error", ex.getMessage());
            logger.error("Kilakona SMS request failed: status={}, body={}", 
                ex.getStatusCode(), ex.getResponseBodyAsString());

            // Save outbox record on provider error
            outboxSmsRepository.save(new OutboxSms(
                dto.getContent(), 
                dto.getPhoneNumber(), 
                "Failed", 
                null, 
                ex.getResponseBodyAsString()
            ));
        } 
        catch (Exception ex) {
            result.put("statusCode", 0);
            result.put("error", ex.getMessage());
            logger.error("Kilakona SMS request error", ex);

            // Save outbox record on unexpected error
            outboxSmsRepository.save(new OutboxSms(
                dto.getContent(), 
                dto.getPhoneNumber(), 
                "Error", 
                null, 
                ex.getMessage()
            ));
        }

        return result;
    }
}


