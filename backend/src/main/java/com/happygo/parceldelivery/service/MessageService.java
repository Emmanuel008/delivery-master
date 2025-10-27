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
    
    @Value("${kilakona.api.url:https://messaging.kilakona.co.tz/api/v1/send-message}")
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json");
        headers.set("api_key", apiKey);
        headers.set("api_secret", apiSecret);

        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", senderId);
        payload.put("messageType", "text");
        payload.put("message", dto.getContent());
        payload.put("contacts", dto.getPhoneNumber());
        payload.put("deliveryReportUrl", deliveryCallback);

        if (logger.isDebugEnabled()) {
            logger.debug("Sending SMS via Kilakona: url={}, senderId={}, contacts={}, hasDeliveryUrl={}, contentLength={}",
                    apiUrl, senderId, dto.getPhoneNumber(), deliveryCallback != null && !deliveryCallback.isEmpty(),
                    dto.getContent() != null ? dto.getContent().length() : 0);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        Map<String, Object> result = new HashMap<>();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            result.put("statusCode", response.getStatusCode().value());

            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> body = mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
                result.put("body", body);
            } catch (Exception ex) {
                result.put("body", response.getBody());
                result.put("parseError", ex.getMessage());
            }

        } catch (RestClientResponseException ex) {
            // Capture non-2xx responses (e.g., 403) and include response body if present
            result.put("statusCode", ex.getRawStatusCode());
            result.put("body", ex.getResponseBodyAsString());
            result.put("error", ex.getMessage());
            logger.warn("Kilakona SMS request failed: status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            result.put("statusCode", 0);
            result.put("error", ex.getMessage());
            logger.error("Kilakona SMS request error", ex);
        }

        return result;
    }
}

