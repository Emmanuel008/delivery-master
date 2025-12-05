package com.happygo.parceldelivery.service;

import com.happygo.parceldelivery.entity.User;
import com.happygo.parceldelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public User createUser(String phoneNumber, String password) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RuntimeException("User with this phone number already exists");
        }
        
        User user = new User(phoneNumber, passwordEncoder.encode(password));
        return userRepository.save(user);
    }
    
    public boolean authenticate(String phoneNumber, String password) {
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        return passwordEncoder.matches(password, user.getPassword());
    }
    
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
}

