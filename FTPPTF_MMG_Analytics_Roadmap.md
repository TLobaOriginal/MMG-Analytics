# FTPPTF / MMG Analytics — Current Project Roadmap

## 1. Project Goal

Build a Spring Boot analytics application that starts with a basic subscription/event system and progressively evolves into an **event-driven analytics architecture**, ultimately incorporating technologies such as **Kafka and InfluxDB**.

The project is being built deliberately in phases so that each architectural concept is understood before moving to the next.

---

# Phase 1 — Project Setup / Foundation

**Status: ✅ COMPLETE**

Completed:

- Created the Spring Boot project.
- Established Maven build.
- Established basic Spring Boot application.
- Confirmed application starts.
- Corrected project/package structure.
- Fixed the test package mismatch.

Important package:

```text
com.FTPPTF.mmganalytics
```

Main application:

```text
src/main/java/com/FTPPTF/mmganalytics/FtpptfApplication.java
```

Test:

```text
src/test/java/com/FTPPTF/mmganalytics/FtpptfApplicationTests.java
```

The initial problem was that the test was under:

```text
com.example.FTPPTF.mmganalytics
```

while the application was under:

```text
com.FTPPTF.mmganalytics
```

This has now been fixed.

`./mvnw clean test` successfully runs the existing tests.

---

# Phase 2 — Initial Data/Event Model

**Status: ✅ COMPLETE**

We established the initial model/repository/service/controller architecture.

The important conceptual distinction established here was between:

### Database model

`SubscriptionEvent`

Represents data that is persisted.

Conceptually:

```text
SubscriptionEvent
├── id
├── eventType
├── timestamp
├── payload
└── other persistence fields
```

### Domain event

Later introduced as:

`SubscriptionCreatedEvent`

Represents something that **happened** in the business/application.

These are deliberately separate objects.

---

# Phase 3 — Application/Event Infrastructure

**Status: ✅ COMPLETE**

The project was prepared for an event-driven architecture.

The important goal was to avoid coupling the business logic directly to Kafka.

Instead, we introduced an abstraction:

```text
Application
    |
    v
EventPublisher
    |
    v
Event infrastructure
```

This means we can initially use Spring's internal event system and later replace it with Kafka without rewriting the application/business logic.

---

# Phase 4 — Event-Driven Architecture

**Status: 🟡 CURRENT PHASE**

This is where we currently are.

---

## Phase 4.1 — Domain Event

**Status: ✅ COMPLETE**

Created:

```text
events/SubscriptionCreatedEvent.java
```

Current package:

```java
package com.FTPPTF.mmganalytics.events;
```

The event currently contains:

```text
eventId
eventType
timestamp
userId
```

Conceptually:

```text
SubscriptionCreatedEvent

"User X created a subscription at time Y."
```

This is a **domain event**, not a database entity.

---

# Phase 4.2 — Event Publisher Abstraction

**Status: ✅ COMPLETE**

Created:

```text
events/EventPublisher.java
```

Interface:

```java
public interface EventPublisher {

    void publish(SubscriptionCreatedEvent event);

}
```

This gives the application a technology-independent publishing abstraction.

Also created:

```text
events/SpringEventPublisher.java
```

This implements `EventPublisher` using Spring's:

```text
ApplicationEventPublisher
```

Current architecture:

```text
Application code
      |
      v
EventPublisher
      |
      v
SpringEventPublisher
      |
      v
Spring Application Event Bus
```

`SpringEventPublisher` is a Spring `@Component`.

---

# Phase 4.3 — Event Listener

**Status: ✅ COMPLETE**

Created:

```text
listener/AnalyticsEventListener.java
```

It is a Spring `@Component` and uses:

```java
@EventListener
```

to receive:

```text
SubscriptionCreatedEvent
```

Current flow:

```text
SubscriptionCreatedEvent
          |
          v
Spring Event Bus
          |
          v
AnalyticsEventListener
```

At this stage the listener currently performs basic/placeholder processing, including logging the user ID.

---

# Phase 4.4 — Connect SubscriptionService to Event Publishing

**Status: ✅ COMPLETE**

Created/wired:

```text
service/SubscriptionService.java
```

Important lesson discovered here:

The service must be annotated:

```java
@Service
```

Without `@Service`, Spring does not register `SubscriptionService` as a bean.

This caused an `UnsatisfiedDependencyException` in the event-flow test, which was fixed by adding `@Service`.

The service now creates a:

```text
SubscriptionCreatedEvent
```

and sends it through:

```text
EventPublisher
```

Conceptually:

```text
SubscriptionService
       |
       | createSubscription()
       v
SubscriptionCreatedEvent
       |
       v
EventPublisher
       |
       v
Spring Event Bus
       |
       v
AnalyticsEventListener
```

---

# Phase 4.5 / 4.6 — Prove the Event Flow

**Status: ✅ COMPLETE**

`EventFlowTest` originally contained:

```java
assertTrue(true);
```

which was deliberately identified as insufficient because it doesn't actually prove that the event was received.

It was rewritten to register its own recording listener (an `@EventListener`-based bean, defined in a `@TestConfiguration` inside the test) that captures whatever `SubscriptionCreatedEvent` reaches the Spring event bus. An earlier attempt used `ApplicationListener<SubscriptionCreatedEvent>` directly, which does not compile — `ApplicationListener<E>` requires `E extends ApplicationEvent`, and domain events are deliberately plain POJOs (see Principle 2). The `@EventListener` annotation form supports arbitrary event types, so that's what the recording listener uses.

The test proves the full pipeline:

```text
SubscriptionService.createSubscription()
        |
        v
EventPublisher
        |
        v
SubscriptionCreatedEvent
        |
        v
Spring Event Bus
        |
        v
Recording listener (test-only) — proves any real @EventListener bean, including
                                   AnalyticsEventListener, would receive the same event
```

The `AtomicInteger`-in-the-production-listener approach discussed earlier was not used — a duplicate listener class had been created while experimenting with it (a typo'd `AnalyticsEvenetListener.java`, double-handling every event alongside the real `AnalyticsEventListener`) and was deleted once the real test made it unnecessary.

---

# Current Project Structure

The structure now looks like:

```text
src
├── main
│   └── java
│       └── com
│           └── FTPPTF
│               └── mmganalytics
│                   │
│                   ├── controller
│                   │
│                   ├── service
│                   │   ├── SubscriptionService.java
│                   │   ├── AnalyticsService.java
│                   │   └── AnalyticsSnapshot.java        (in-memory record)
│                   │
│                   ├── model
│                   │   ├── SubscriptionEvent.java
│                   │   └── AnalyticsSnapshotEntity.java  (persisted form)
│                   │
│                   ├── repository
│                   │   ├── SubscriptionEventRepository.java
│                   │   └── AnalyticsSnapshotRepository.java
│                   │
│                   ├── events
│                   │   ├── SubscriptionCreatedEvent.java
│                   │   ├── EventPublisher.java
│                   │   └── SpringEventPublisher.java
│                   │
│                   ├── listener
│                   │   └── AnalyticsEventListener.java
│                   │
│                   └── FtpptfApplication.java
│
└── test
    └── java
        └── com
            └── FTPPTF
                └── mmganalytics
                    ├── FtpptfApplicationTests.java
                    │
                    ├── events
                    │   └── EventFlowTest.java
                    │
                    └── service
                        └── AnalyticsServiceTest.java
```

---

## 4.7 — Make AnalyticsEventListener Actually Do Analytics Work

**Status: ✅ COMPLETE**

`AnalyticsEventListener` now constructor-injects `AnalyticsService` and delegates to it instead of just logging:

```text
SubscriptionCreatedEvent
          |
          v
AnalyticsEventListener
          |
          v
AnalyticsService.recordSubscriptionCreated(event)
          |
          v
Persists a SubscriptionEvent row (eventType translated to "SUBSCRIBE")
```

Worth noting: the domain event's `eventType` (`"SUBSCRIPTION_CREATED"`) and the DB model's `eventType` (`"SUBSCRIBE"` / `"UNSUBSCRIBE"`) are different vocabularies that happen to share a field name. `AnalyticsService` is the explicit translation boundary between them — it does not pass the domain event's string straight through.

Proven by `AnalyticsServiceTest`, which asserts `getTotalSubscriptions()`/`getNetGrowth()` change by the expected delta after calling `createSubscription()` — not just that nothing crashed.

---

## 4.8 — Introduce Analytics State/Snapshot

**Status: ✅ COMPLETE**

Added `AnalyticsSnapshot` (a Java `record` in the `service` package) with the minimal aggregate scope decided on: `totalSubscriptions`, `totalUnsubscriptions`, `netGrowth`, `generatedAt`. Broader scope (per-user breakdown, time-windowed buckets) was considered and deliberately deferred — the roadmap's own principle is not to pull time-series concerns in prematurely.

`AnalyticsService.getSnapshot()` builds one by composing the existing read methods plus a timestamp. It is computed fresh on every call — nothing is cached or stored yet.

---

## 4.9 — Persistence of Analytics

**Status: 🟡 FIRST CUT COMPLETE — trigger strategy still open**

Added the persisted counterpart to `AnalyticsSnapshot`:

```text
AnalyticsSnapshot (in-memory)  →  AnalyticsSnapshotEntity (JPA, model package)
                                          |
                                          v
                                 AnalyticsSnapshotRepository
```

`AnalyticsService.saveSnapshot()` computes a snapshot and writes it — **on demand only**. This was a deliberate choice among three options (on-demand, save-per-event, scheduled): saving per-event was rejected because a snapshot represents a point-in-time rollup, not a per-event record; scheduled was rejected as premature infrastructure ahead of the InfluxDB decision. On-demand-only means the persisted snapshot history is currently empty — nothing calls `saveSnapshot()` in production code yet.

**Open decision:** what should actually trigger `saveSnapshot()` — event-driven, scheduled, or something else — is deferred, and is tied directly to the Kafka-vs-InfluxDB direction below. Don't assume an answer here; it needs to be decided deliberately, the same way the scope and trigger-type questions above were.

---

# Later Architecture — Target Direction

The eventual architecture we're building toward is roughly:

```text
                   User / Client
                         |
                         v
                    Controller
                         |
                         v
                SubscriptionService
                         |
                         v
              SubscriptionCreatedEvent
                         |
                         v
                  EventPublisher
                         |
                         v
                       Kafka
                         |
             +-----------+-----------+
             |                       |
             v                       v
      Analytics Consumer       Other Consumers
             |
             v
      AnalyticsService
             |
             v
       Analytics State
             |
             v
          InfluxDB
```

The important architectural distinction will eventually be:

### Kafka

Used for **event transport / event streaming**.

```text
"What happened?"
```

and distributing those events to consumers.

### InfluxDB

Used for **time-series storage and analytics**.

```text
"What measurements do we have over time?"
```

They solve different problems and can work together.

---

# Important Principles We've Established

Claude Code should preserve these architectural decisions:

### 1. Don't couple business logic directly to Kafka

Use:

```text
EventPublisher
```

as the abstraction.

### 2. Domain events are not database entities

Keep:

```text
SubscriptionEvent
```

and:

```text
SubscriptionCreatedEvent
```

separate.

### 3. Spring components need to be registered

For example:

```java
@Service
```

for services and:

```java
@Component
```

for components such as publishers/listeners.

### 4. Test behaviour, not merely compilation

We want tests that prove:

```text
something happened
```

rather than:

```text
nothing crashed
```

### 5. Build incrementally

Don't introduce Kafka/InfluxDB prematurely.

First prove:

```text
Application
   ↓
Domain Event
   ↓
Publisher
   ↓
Event Bus
   ↓
Listener
   ↓
Analytics
```

Then replace the appropriate infrastructure with Kafka/InfluxDB.

---

# Current Status in One Line

**Phases 1–3 are complete; Phase 4.1–4.9 are complete through the first cut of snapshot persistence (`AnalyticsService.saveSnapshot()`, on-demand only, nothing calls it automatically yet).**

**Do not jump straight to Kafka.** The next decision is what triggers `saveSnapshot()` to actually run — that choice is what determines how the Kafka-vs-InfluxDB split gets introduced, so it should be made deliberately rather than assumed.
