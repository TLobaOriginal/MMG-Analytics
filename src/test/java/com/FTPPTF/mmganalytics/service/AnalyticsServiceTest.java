package com.FTPPTF.mmganalytics.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.FTPPTF.mmganalytics.model.AnalyticsSnapshotEntity;
import com.FTPPTF.mmganalytics.repository.AnalyticsSnapshotRepository;

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

    @Autowired
    private AnalyticsSnapshotRepository snapshotRepository;

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

    @Test
    void savingASnapshotPersistsItToTheDatabase() {
        subscriptionService.createSubscription("userSnap");

        AnalyticsSnapshot expected = analyticsService.getSnapshot();
        AnalyticsSnapshotEntity saved = analyticsService.saveSnapshot();

        assertThat(saved.getId()).isNotNull();

        Optional<AnalyticsSnapshotEntity> reloaded = snapshotRepository.findById(saved.getId());
        assertThat(reloaded)
            .as("saveSnapshot() should leave a row that can be read back from the database")
            .isPresent();
        assertThat(reloaded.get().getTotalSubscriptions()).isEqualTo(expected.totalSubscriptions());
        assertThat(reloaded.get().getTotalUnsubscriptions()).isEqualTo(expected.totalUnsubscriptions());
        assertThat(reloaded.get().getNetGrowth()).isEqualTo(expected.netGrowth());
    }
}
