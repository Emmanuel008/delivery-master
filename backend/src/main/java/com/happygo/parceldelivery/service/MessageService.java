package com.happygo.parceldelivery.service;

import com.happygo.parceldelivery.dto.MessageDto;
import com.happygo.parceldelivery.entity.Message;
import com.happygo.parceldelivery.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
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
}
