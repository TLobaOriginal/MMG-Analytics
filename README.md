# MMG Analytics System

A Spring Boot-based event-driven analytics system designed to ingest, store, and analyse subscription events in real time. The project simulates a scalable backend architecture for tracking user engagement metrics such as subscriptions and unsubscriptions over time.

---

## 🚀 MVP (Current Implementation)

The current system provides:

### 🧱 Core Backend
- Spring Boot REST API for event ingestion
- PostgreSQL database integration (via Docker)
- Clean layered architecture:
  - Controller → Service → Repository → Database

### 📡 Event System
- Supports subscription and unsubscription events
- Stores event metadata including:
  - event type
  - timestamp
  - payload (user information)

### 🗄️ Data Persistence
- Events are persisted in a relational database
- Duplicate handling designed using composite identifiers (event id + user id concept)

### 🧪 Basic API Endpoints
- `POST /events` → Ingest new event
- `GET /events` → Retrieve stored events

---

## 🏗️ Architecture
Controller → Service → Repository → PostgreSQL


Designed with separation of concerns to support scalability and future extension into event-driven or streaming architectures.

---

## 📊 Planned Features (Roadmap)

### Phase 3 — Analytics Engine
- Net subscription growth tracking
- Time-based aggregations (daily / monthly / hourly)
- Subscriber gain vs loss ratios
- Query-based analytics endpoints

### Phase 4 — Event-Driven Architecture
- Kafka-based event streaming
- Asynchronous event processing
- Scalable consumer architecture

### Phase 5 — Reliability & Observability
- Retry mechanisms for failed events
- Logging and monitoring improvements
- Failure alerting (email/notification system)

### Phase 6 — Dashboard (Optional)
- Frontend analytics dashboard
- Real-time visualisation of subscription metrics

---

## 🧠 Key Concepts Demonstrated

- REST API design
- Spring Boot architecture patterns
- Database design and persistence
- Event-driven system thinking
- Clean code and layered architecture
- Docker-based infrastructure setup

---

## 🎯 Goal of This Project

This project is a hands-on exploration of building scalable backend systems inspired by real-world analytics platforms. It focuses on learning how raw event data is transformed into meaningful insights through structured backend design.

---

## 🔧 Tech Stack

- Java
- Spring Boot
- PostgreSQL
- Docker
- Maven
- Git / GitHub
