package com.FTPPTF.mmganalytics.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Proves the effect, not just the delivery, of the event pipeline:
 *
 *   SubscriptionService.createSubscription() -> ... -> AnalyticsService
 *   should leave AnalyticsService's read methods reflecting the change.
 *
 * Runs against the real database, so assertions are made on deltas rather
 * than absolute counts - the table may already contain unrelated rows.
 * @Transactional rolls back everything this test writes.
 */
@SpringBootTest
@Transactional
class AnalyticsServiceTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AnalyticsService analyticsService;

    @Test
    void creatingASubscriptionIncreasesTotalSubscriptions() {
        long before = analyticsService.getTotalSubscriptions();

        subscriptionService.createSubscription("user456");

        assertThat(analyticsService.getTotalSubscriptions())
            .as("recording a SubscriptionCreatedEvent should persist a SUBSCRIBE row")
            .isEqualTo(before + 1);
    }

    @Test
    void creatingMultipleSubscriptionsIncreasesTotalByThatCount() {
        long before = analyticsService.getTotalSubscriptions();

        subscriptionService.createSubscription("user1");
        subscriptionService.createSubscription("user2");
        subscriptionService.createSubscription("user3");

        assertThat(analyticsService.getTotalSubscriptions()).isEqualTo(before + 3);
    }

    @Test
    void creatingASubscriptionIncreasesNetGrowth() {
        long before = analyticsService.getNetGrowth();

        subscriptionService.createSubscription("user789");

        assertThat(analyticsService.getNetGrowth()).isEqualTo(before + 1);
    }

    @Test
    void snapshotReflectsCurrentTotalsAtTheMomentItWasTaken() {
        AnalyticsSnapshot before = analyticsService.getSnapshot();

        subscriptionService.createSubscription("user321");

        AnalyticsSnapshot after = analyticsService.getSnapshot();

        assertThat(after.totalSubscriptions()).isEqualTo(before.totalSubscriptions() + 1);
        assertThat(after.totalUnsubscriptions()).isEqualTo(before.totalUnsubscriptions());
        assertThat(after.netGrowth()).isEqualTo(before.netGrowth() + 1);
        assertThat(after.generatedAt())
            .as("generatedAt should be a real, current timestamp, not left unset")
            .isAfterOrEqualTo(before.generatedAt())
            .isBeforeOrEqualTo(Instant.now());
    }
}
