package com.FTPPTF.mmganalytics.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FTPPTF.mmganalytics.model.SubscriptionEvent;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SubscriptionEventRepository 
extends JpaRepository<SubscriptionEvent, UUID> {

        long countByEventType(String eventType);

        @Query("SELECT e FROM SubscriptionEvent e WHERE e.eventType =: type")
        List<SubscriptionEvent> findByType(String type);
}