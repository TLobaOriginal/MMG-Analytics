package com.FTPPTF.mmganalytics.controller;
import org.springframework.web.bind.annotation.*;

import com.FTPPTF.mmganalytics.service.AnalyticsService;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    
    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service){
        this.service = service;
    }

    @GetMapping("/total-subscriptions")
    public long totalSubscriptions(){
        return service.getTotalSubscriptions();
    }

    @GetMapping("/total-unsubscriptions")
    public long totalUnsubscriptions(){
        return service.getTotalUnsubscriptions();
    }

    @GetMapping("/net-growth")
    public long netGrowth(){
        return service.getNetGrowth();
    }
}
