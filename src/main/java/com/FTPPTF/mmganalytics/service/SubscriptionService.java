package com.FTPPTF.mmganalytics.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.FTPPTF.mmganalytics.events.EventPublisher;
import com.FTPPTF.mmganalytics.events.SubscriptionCreatedEvent;

@Service
public class SubscriptionService {
    
    private final EventPublisher eventPublisher;

    public SubscriptionService(EventPublisher eventPublisher){
        this.eventPublisher = eventPublisher;
    }

    public void createSubscription(String userId){
        SubscriptionCreatedEvent event = new SubscriptionCreatedEvent(1L, 
            "SUBSCRIPTION_CREATED", 
            LocalDateTime.now(), userId);

        eventPublisher.publish(event);
    }
}
