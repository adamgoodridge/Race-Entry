package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    //allows for the count field
    private final EventViewRepository eventViewRepository;
    private final EntryRepository entryRepository;

    @Autowired
    public EventService(EventRepository eventRepository, EventViewRepository eventViewRepository, EntryRepository entryRepository) {
        this.eventRepository = eventRepository;
        this.eventViewRepository = eventViewRepository;
        this.entryRepository = entryRepository;
    }

    public List<EventView> getEventsView(int open) {
        return eventViewRepository.events(open);
    }


    public List<Event> getOpenEvents() {
        return eventRepository.openEvents();
    }


    public Event getEvent(long id) {
        Event event = eventRepository.getReferenceById(id);
        event.setDescription(StringEscapeUtils.unescapeJava(event.getDescription()));
        event.setDeclaration(StringEscapeUtils.escapeJava(event.getDeclaration()));
        return event;
    }


    public void updateEvent(Event event) {
        //lazy load will mean that foregin keys will be load
        Event proxy = eventRepository.getReferenceById(event.getEventId());
        proxy.setName(event.getName());
        proxy.setVenue(event.getVenue());
        proxy.setStartDate(event.getStartDate());
        proxy.setDuration(event.getDuration());
        //format into unicode to avoid the error with some character inserting the dn
        proxy.setDescription(StringEscapeUtils.escapeJava(event.getDescription()));
        proxy.setDeclaration(StringEscapeUtils.escapeJava(event.getDeclaration()));
        proxy.setOpen(event.getOpen());
        eventRepository.save(proxy);
    }


    public void deleteEvent(long id) {
        Event event = eventRepository.getReferenceById(id);
        List<Entry> entries = entryRepository.entriesByEvent(id);
        event.setEntries(entries);
        eventRepository.delete(event);
    }


    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}
