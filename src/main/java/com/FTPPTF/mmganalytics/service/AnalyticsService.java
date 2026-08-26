package com.FTPPTF.mmganalytics.service;
import org.springframework.stereotype.Service;

import com.FTPPTF.mmganalytics.events.SubscriptionCreatedEvent;
import com.FTPPTF.mmganalytics.model.AnalyticsSnapshotEntity;
import com.FTPPTF.mmganalytics.model.SubscriptionEvent;
import com.FTPPTF.mmganalytics.repository.AnalyticsSnapshotRepository;
import com.FTPPTF.mmganalytics.repository.SubscriptionEventRepository;

import java.time.Instant;
import java.time.ZoneId;

@Service
public class AnalyticsService {
    private final SubscriptionEventRepository repository;
    private final AnalyticsSnapshotRepository snapshotRepository;

    public AnalyticsService(SubscriptionEventRepository repository, AnalyticsSnapshotRepository snapshotRepository){
        this.repository = repository;
        this.snapshotRepository = snapshotRepository;
    }

    public void recordSubscriptionCreated(SubscriptionCreatedEvent event) {
        SubscriptionEvent record = new SubscriptionEvent();
        record.setUserId(event.getUserId());
        record.setEventType("SUBSCRIBE");
        record.setTimeStamp(event.getTimestamp().atZone(ZoneId.systemDefault()).toInstant());
        repository.save(record);
    }

    public long getTotalSubscriptions(){ //Stream allows for operations
        return repository.countByEventType("SUBSCRIBE");
    }

    public long getTotalUnsubscriptions(){
        return repository.countByEventType("UNSUBSCRIBE");
    }

    public long getNetGrowth(){
        return getTotalSubscriptions() - getTotalUnsubscriptions();
    }

    public AnalyticsSnapshot getSnapshot() {
        return new AnalyticsSnapshot(
            getTotalSubscriptions(),
            getTotalUnsubscriptions(),
            getNetGrowth(),
            Instant.now()
        );
    }

    public AnalyticsSnapshotEntity saveSnapshot() {
        AnalyticsSnapshot snapshot = getSnapshot();
        AnalyticsSnapshotEntity entity = new AnalyticsSnapshotEntity(
            snapshot.totalSubscriptions(),
            snapshot.totalUnsubscriptions(),
            snapshot.netGrowth(),
            snapshot.generatedAt()
        );
        return snapshotRepository.save(entity);
    }
}
