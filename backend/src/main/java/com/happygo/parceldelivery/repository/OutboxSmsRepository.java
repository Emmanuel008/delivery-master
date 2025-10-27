package com.happygo.parceldelivery.repository;

import com.happygo.parceldelivery.entity.OutboxSms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxSmsRepository extends JpaRepository<OutboxSms, Long> {
}
