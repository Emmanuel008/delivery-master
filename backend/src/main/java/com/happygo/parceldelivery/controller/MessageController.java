package com.happygo.parceldelivery.controller;

import com.happygo.parceldelivery.dto.MessageDto;
import com.happygo.parceldelivery.entity.Message;
import com.happygo.parceldelivery.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
