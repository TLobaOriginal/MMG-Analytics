package com.FTPPTF.mmganalytics.events;

import java.time.LocalDateTime;
/* Domain Event NOT DATABASE */
public class SubscriptionCreatedEvent {

    private final Long eventId;
    private final String eventType;
    private final LocalDateTime timestamp;
    private final String userId;

    public SubscriptionCreatedEvent(
        Long eventId,
        String eventType,
        LocalDateTime timestamp,
        String userId
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
}
