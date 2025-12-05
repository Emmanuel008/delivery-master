package com.happygo.parceldelivery.controller;

import com.happygo.parceldelivery.dto.LoginDto;
import com.happygo.parceldelivery.dto.LoginResponseDto;
import com.happygo.parceldelivery.entity.User;
import com.happygo.parceldelivery.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            boolean authenticated = userService.authenticate(
                loginDto.getPhoneNumber(), 
                loginDto.getPassword()
            );
            
            if (authenticated) {
                User user = userService.findByPhoneNumber(loginDto.getPhoneNumber())
                    .orElseThrow(() -> new RuntimeException("User not found"));
                
                // Generate a simple token (in production, use JWT)
                String token = UUID.randomUUID().toString();
                
                LoginResponseDto.UserInfo userInfo = new LoginResponseDto.UserInfo(
                    user.getId(),
                    user.getPhoneNumber()
                );
                
                LoginResponseDto response = new LoginResponseDto(
                    true,
                    "Login successful",
                    token,
                    userInfo
                );
                
                return ResponseEntity.ok(response);
            } else {
                LoginResponseDto response = new LoginResponseDto(
                    false,
                    "Invalid phone number or password"
                );
                return ResponseEntity.status(401).body(response);
            }
        } catch (Exception e) {
            LoginResponseDto response = new LoginResponseDto(
                false,
                "Login failed: " + e.getMessage()
            );
            return ResponseEntity.status(500).body(response);
        }
    }
}

