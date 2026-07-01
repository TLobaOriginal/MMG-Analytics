package com.FTPPTF.mmganalytics.model;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
/**
 * 
 * SubscriptionEvent
 * Including...
 * Id
 * eventType (SUBSCRIBE / UNSUBSCRIBE)
 * timeStamp
 * payload (User info)
 */
@Entity
public class SubscriptionEvent {
    
    @Id
    @GeneratedValue
    private UUID id;

    private String userId;

    private String eventType; // SUBSCRIBE or UNSUBSCRIBE

    private Instant timeStamp;

    @Column(columnDefinition = "TEXT")
    private String payload;

    public SubscriptionEvent(){

    }

    //getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventyType) {
        this.eventType = eventyType;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

}
