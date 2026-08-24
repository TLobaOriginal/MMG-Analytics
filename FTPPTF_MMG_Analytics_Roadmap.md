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

**Status: 🟡 CURRENT WORK**

We created:

```text
src/test/java/com/FTPPTF/mmganalytics/events/EventFlowTest.java
```

The initial version contained:

```java
assertTrue(true);
```

which was deliberately identified as insufficient because it doesn't actually prove that the event was received.

We are currently improving this test so that it verifies the actual event pipeline.

The intended test should prove:

```text
SubscriptionService
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
AnalyticsEventListener
```

and specifically verify that the listener actually received the event.

We discussed temporarily using an `AtomicInteger` inside `AnalyticsEventListener` to count received events, but this is not necessarily the final production-quality testing approach.

**Current immediate task:**

Finish a meaningful `EventFlowTest` that genuinely verifies event delivery.

---

# Current Project Structure

The important structure should now resemble:

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
│                   │   └── SubscriptionService.java
│                   │
│                   ├── model
│                   │   └── SubscriptionEvent.java
│                   │
│                   ├── repository
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
                    └── events
                        └── EventFlowTest.java
```

---

# Phase 4 Remaining Work

After we successfully prove the event flow, the remaining Phase 4 work should progress roughly as follows.

## 4.6 — Proper Event Flow Test

**Current task**

Create a real integration test proving:

```text
createSubscription()
        ↓
event published
        ↓
listener receives event
```

Not merely "the application didn't crash."

---

## 4.7 — Make AnalyticsEventListener Actually Do Analytics Work

Currently it is primarily a demonstration listener.

Next, it should hand the event to the analytics layer.

Conceptually:

```text
SubscriptionCreatedEvent
          |
          v
AnalyticsEventListener
          |
          v
AnalyticsService
          |
          v
Update analytics state
```

This separates:

- event reception
- analytics/business logic

---

## 4.8 — Introduce Analytics State/Snapshot

We need to establish what we're actually measuring.

For example:

```text
total subscriptions
subscriptions per time period
subscriptions per user
event counts
etc.
```

The exact analytics model should be established before introducing the time-series database.

---

## 4.9 — Persistence of Analytics

Introduce the persistence layer for the analytics data/snapshots.

This is where the project starts moving toward the eventual comparison/use of:

```text
Kafka
vs
InfluxDB
```

Kafka and InfluxDB have different responsibilities, so they should **not** be treated as direct substitutes.

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

**Phases 1–3 are complete; Phase 4.1–4.4 are complete; we are currently finishing Phase 4.6 by writing a real integration test proving that `SubscriptionService` publishes `SubscriptionCreatedEvent` and `AnalyticsEventListener` receives it.**

Once that test passes, **do not jump straight to Kafka**. The next step is to make the listener feed a proper analytics service/state model, then establish the analytics persistence requirements before introducing the infrastructure technologies.
