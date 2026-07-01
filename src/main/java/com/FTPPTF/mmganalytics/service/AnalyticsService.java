package com.FTPPTF.mmganalytics.service;
import org.springframework.stereotype.Service;

import com.FTPPTF.mmganalytics.repository.SubscriptionEventRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {
    private final SubscriptionEventRepository repository;

    public AnalyticsService(SubscriptionEventRepository repository){
        this.repository = repository;
    }

    public long getTotalSubscriptions(){ //Stream allows for operations
        return repository.countByEventType("SUBSCRIBE");
    }

    public long getTotalUnsubscriptions(){
        return repository.countByEventType("UBSUBSCRIBE");
    }

    public long getNetGrowth(){
        return getTotalSubscriptions() - getTotalUnsubscriptions();
    }
}
