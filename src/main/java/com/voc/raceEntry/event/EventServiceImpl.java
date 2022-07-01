package com.voc.raceEntry.event;

import com.voc.raceEntry.entry.Entry;
import com.voc.raceEntry.entry.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    //allows for the count field
    private EventViewRepository eventViewRepository;
    @Autowired
    private EntryRepository entryRepository;

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
    public void deleteEvent(long id) {
        Event event = eventRepository.getReferenceById(id);
        List<Entry> entries = entryRepository.entriesByEvent(id);
        event.setEntries(entries);
        eventRepository.delete(event);
    }

    @Override
    public List<Event> getAllEvents() {
        System.out.println(eventRepository.findAll());
        return eventRepository.findAll();
    }
}
