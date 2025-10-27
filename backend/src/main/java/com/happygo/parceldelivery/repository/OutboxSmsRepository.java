package com.happygo.parceldelivery.repository;

import com.happygo.parceldelivery.entity.OutboxSms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxSmsRepository extends JpaRepository<OutboxSms, Long> {
    Optional<OutboxSms> findByMessageId(String messageId);
    List<OutboxSms> findAllByOrderByCreatedAtDesc();
}
