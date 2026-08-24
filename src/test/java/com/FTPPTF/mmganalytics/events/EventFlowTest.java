package com.FTPPTF.mmganalytics.events;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import com.FTPPTF.mmganalytics.service.SubscriptionService;

/**
 * Proves the real event pipeline end to end:
 *
 *   SubscriptionService -> EventPublisher -> Spring event bus -> listeners
 *
 * Rather than wiring a counter into the production AnalyticsEventListener
 * just so a test can read it, this test registers its own recording
 * listener (below) purely inside the test context. If this test passes,
 * it proves that ANY real @EventListener bean - including
 * AnalyticsEventListener - would have received the same event, because
 * they all subscribe to the same Spring event bus.
 *
 * AnalyticsEventListener now persists a SubscriptionEvent row via
 * AnalyticsService, so this test runs inside a transaction that is rolled
 * back after each test method - otherwise every run would leave a permanent
 * "user123" row in the real database.
 */
@SpringBootTest
@Import(EventFlowTest.RecordingListenerConfig.class)
@Transactional
class EventFlowTest {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private RecordedEvents recordedEvents;

    @BeforeEach
    void clearRecordedEvents() {
        // @SpringBootTest reuses the same Spring context (and therefore the
        // same RecordedEvents bean) across test methods, so each test needs
        // a clean slate.
        recordedEvents.events().clear();
    }

    @Test
    void subscriptionCreatesEventThatReachesListeners() {
        subscriptionService.createSubscription("user123");

        assertThat(recordedEvents.events())
            .as("a listener registered on the Spring event bus should have received the published event")
            .hasSize(1);

        SubscriptionCreatedEvent received = recordedEvents.events().get(0);
        assertThat(received.getUserId()).isEqualTo("user123");
        assertThat(received.getEventType()).isEqualTo("SUBSCRIPTION_CREATED");
    }

    /** Holds whatever the recording listener below captures, so the test can assert on it. */
    static class RecordedEvents {
        private final List<SubscriptionCreatedEvent> events = new CopyOnWriteArrayList<>();

        List<SubscriptionCreatedEvent> events() {
            return events;
        }
    }

    /** Test-only beans: never shipped in the production application context. */
    @TestConfiguration
    static class RecordingListenerConfig {

        @Bean
        RecordedEvents recordedEvents() {
            return new RecordedEvents();
        }

        @Bean
        Object recordingListener(RecordedEvents recordedEvents) {
            return new Object() {
                @EventListener
                void onSubscriptionCreated(SubscriptionCreatedEvent event) {
                    recordedEvents.events().add(event);
                }
            };
        }
    }
}
