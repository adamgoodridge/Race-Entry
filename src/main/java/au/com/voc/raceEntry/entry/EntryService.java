package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EntryService {

    private final EntryRepository entryRepository;
    private final EventRepository eventRepository;

    public EntryService(EntryRepository entryRepository, EventRepository eventRepository) {
        this.entryRepository = entryRepository;
        this.eventRepository = eventRepository;
    }

    public List<Entry> findByEvent(Event event) {
        return entryRepository.findByEvent(event);
    }

    // Guard that replaces the old @PreRemove on Event.
    // Throws if the event has existing entries, then deletes.
    public void deleteEvent(Event event) {
        if (entryRepository.existsByEvent(event)) {
            throw new IllegalStateException(
                "Cannot delete event '" + event.getName() + "': it has existing entries"
            );
        }
        eventRepository.delete(event);
    }
}
