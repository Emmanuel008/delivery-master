package com.happygo.parceldelivery.service;

import com.happygo.parceldelivery.dto.MessageDto;
import com.happygo.parceldelivery.entity.Message;
import com.happygo.parceldelivery.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
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
    
    @Value("${kilakona.api.url:https://messaging.kilakona.co.tz/api/v1/vendor/message/send}")
    private String apiUrl;
    
    @Value("${kilakona.api.key:MY_API_KEY}")
    private String apiKey;
    
    @Value("${kilakona.api.secret:MY_API_SECRET}")
    private String apiSecret;
    
    @Value("${kilakona.sender.id:MY_SENDER_ID}")
    private String senderId;
    
    @Value("${kilakona.delivery.callback:''}")
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

    public Map<String, Object> sendSms(MessageDto dto) {
        RestTemplate restTemplate = new RestTemplate();

        // --- Set headers exactly as in cURL ---
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api_key", apiKey);
        headers.set("api_secret", apiSecret);

        // --- Match payload exactly to the cURL request ---
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", senderId);
        payload.put("messageType", "text");
        payload.put("message", dto.getContent());
        payload.put("contacts", dto.getPhoneNumber().replace("+", "")); // API expects without '+'
        payload.put("deliveryReportUrl", deliveryCallback != null ? deliveryCallback : "");

        // Log request info
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
        } 
        catch (RestClientResponseException ex) {
            result.put("statusCode", ex.getRawStatusCode());
            result.put("body", ex.getResponseBodyAsString());
            result.put("error", ex.getMessage());
            logger.error("Kilakona SMS request failed: status={}, body={}", 
                ex.getRawStatusCode(), ex.getResponseBodyAsString());
        } 
        catch (Exception ex) {
            result.put("statusCode", 0);
            result.put("error", ex.getMessage());
            logger.error("Kilakona SMS request error", ex);
        }

        return result;
    }
    
    // Getter methods for debugging
    public String getApiUrl() {
        return apiUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public String getDeliveryCallback() {
        return deliveryCallback;
    }
}


