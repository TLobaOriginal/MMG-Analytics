package com.FTPPTF.mmganalytics.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * Persisted form of AnalyticsSnapshot (service package) - a saved rollup of
 * analytics totals at a point in time.
 */
@Entity
public class AnalyticsSnapshotEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private long totalSubscriptions;

    private long totalUnsubscriptions;

    private long netGrowth;

    private Instant generatedAt;

    public AnalyticsSnapshotEntity() {
    }

    public AnalyticsSnapshotEntity(long totalSubscriptions, long totalUnsubscriptions, long netGrowth, Instant generatedAt) {
        this.totalSubscriptions = totalSubscriptions;
        this.totalUnsubscriptions = totalUnsubscriptions;
        this.netGrowth = netGrowth;
        this.generatedAt = generatedAt;
    }

    public UUID getId() {
        return id;
    }

    public long getTotalSubscriptions() {
        return totalSubscriptions;
    }

    public long getTotalUnsubscriptions() {
        return totalUnsubscriptions;
    }

    public long getNetGrowth() {
        return netGrowth;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}
