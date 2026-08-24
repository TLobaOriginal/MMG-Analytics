package com.FTPPTF.mmganalytics.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.FTPPTF.mmganalytics.events.SubscriptionCreatedEvent;
import com.FTPPTF.mmganalytics.service.AnalyticsService;

@Component
public class AnalyticsEventListener {

    private final AnalyticsService analyticsService;

    public AnalyticsEventListener(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @EventListener
    public void handleSubscriptionCreated(
        SubscriptionCreatedEvent event
    ){
        analyticsService.recordSubscriptionCreated(event);
    }

}
