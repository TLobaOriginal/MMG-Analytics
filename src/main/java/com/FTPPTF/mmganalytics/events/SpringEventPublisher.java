package com.FTPPTF.mmganalytics.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringEventPublisher implements EventPublisher{
    
    private final ApplicationEventPublisher publisher;

    public SpringEventPublisher(ApplicationEventPublisher publisher){
        this.publisher = publisher;
    }

    @Override
    public void publish(SubscriptionCreatedEvent event){
        publisher.publishEvent(event);
    }
}
