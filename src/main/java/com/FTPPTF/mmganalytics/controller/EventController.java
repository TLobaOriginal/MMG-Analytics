package com.FTPPTF.mmganalytics.controller;
import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.FTPPTF.mmganalytics.model.SubscriptionEvent;
import com.FTPPTF.mmganalytics.repository.SubscriptionEventRepository;

@RestController
@RequestMapping("/events")
public class EventController {
    
    private final SubscriptionEventRepository repo;
    
    public EventController(SubscriptionEventRepository repo){
        this.repo = repo;
    }

    @PostMapping
    public SubscriptionEvent create(@RequestBody SubscriptionEvent event){
        event.setTimeStamp(Instant.now());
        return repo.save(event);
    }

    @GetMapping
    public List<SubscriptionEvent> getAll(){
        return repo.findAll();
    }
}
