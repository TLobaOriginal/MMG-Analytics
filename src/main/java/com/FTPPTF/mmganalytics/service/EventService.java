package com.FTPPTF.mmganalytics.service;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.FTPPTF.mmganalytics.model.SubscriptionEvent;
import com.FTPPTF.mmganalytics.repository.SubscriptionEventRepository;
/** WILL HADNLE THE LOGIC 
 * Can test logic without HTTP
 * Future logic can be built in here without
*/
@Service
public class EventService {
    private final SubscriptionEventRepository repository; //Have our repo set in the Service class

    public EventService(SubscriptionEventRepository repository){ //Constructor
        this.repository = repository;
    }

    //Save event
    public SubscriptionEvent createEvent(SubscriptionEvent event){
        event.setTimeStamp(Instant.now());
        return repository.save(event);
    }
    
    //Fetch all events
    public List<SubscriptionEvent> getAllEvents(){
        return repository.findAll();
    }

    
}
