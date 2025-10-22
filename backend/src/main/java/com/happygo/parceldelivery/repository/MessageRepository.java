package com.happygo.parceldelivery.repository;

import com.happygo.parceldelivery.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findAllByOrderByCreatedAtDesc();
}
