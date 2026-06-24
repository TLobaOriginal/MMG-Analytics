package com.FTPPTF.mmganalytics.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.FTPPTF.mmganalytics.model.SubscriptionEvent;

public interface SubscriptionEventRepository
        extends JpaRepository<SubscriptionEvent, UUID> {
}