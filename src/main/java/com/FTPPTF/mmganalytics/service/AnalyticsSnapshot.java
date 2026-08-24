package com.FTPPTF.mmganalytics.service;

import java.time.Instant;

public record AnalyticsSnapshot(
    long totalSubscriptions,
    long totalUnsubscriptions,
    long netGrowth,
    Instant generatedAt
) {}
