package com.voc.raceEntry.Service;

import com.voc.raceEntry.Entity.Event;
import com.voc.raceEntry.Entity.EventView;
import com.voc.raceEntry.dao.EventRepository;
import com.voc.raceEntry.dao.EventViewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService{
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    //allows for the count field
    private EventViewRepository eventViewRepository;

    @Override
    public List<EventView> getOpenEventsView() {
        return eventViewRepository.findAll();
    }

    @Override
    public List<Event> getOpenEvents() {
        return eventRepository.openEvents();
    }

    @Override
    public Event getEvent(long id) {
        return eventRepository.getReferenceById(id);
    }

    @Override
    public void updateEvent(Event event) {
        eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents() {
        System.out.println(eventRepository.findAll());
        return eventRepository.findAll();
    }
}
