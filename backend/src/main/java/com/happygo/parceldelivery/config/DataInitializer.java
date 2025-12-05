package com.happygo.parceldelivery.config;

import com.happygo.parceldelivery.entity.User;
import com.happygo.parceldelivery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if it doesn't exist
        String defaultPhoneNumber = "255625313162";
        String defaultPassword = "admin";
        
        if (!userRepository.existsByPhoneNumber(defaultPhoneNumber)) {
            User adminUser = new User();
            adminUser.setPhoneNumber(defaultPhoneNumber);
            adminUser.setPassword(passwordEncoder.encode(defaultPassword));
            userRepository.save(adminUser);
            System.out.println("Default admin user created: " + defaultPhoneNumber);
        }
    }
}

