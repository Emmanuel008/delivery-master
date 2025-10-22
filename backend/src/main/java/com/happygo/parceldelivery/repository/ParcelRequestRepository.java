package com.happygo.parceldelivery.repository;

import com.happygo.parceldelivery.entity.ParcelRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelRequestRepository extends JpaRepository<ParcelRequest, Long> {
    
    List<ParcelRequest> findByIsReadFalseOrderByCreatedAtDesc();
    
    @Query("SELECT COUNT(p) FROM ParcelRequest p WHERE p.isRead = false")
    Long countUnreadRequests();
    
    List<ParcelRequest> findAllByOrderByCreatedAtDesc();
}
