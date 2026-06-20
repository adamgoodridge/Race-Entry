package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.exception.BusinessRuleViolationException;
import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final BoatRepository boatRepository;
    private final EventRepository eventRepository;
    private final EntryDriverRepository driverRepository;

    public EntryService(EntryRepository entryRepository, BoatRepository boatRepository,
                        EventRepository eventRepository, EntryDriverRepository driverRepository) {
        this.entryRepository = entryRepository;
        this.boatRepository = boatRepository;
        this.eventRepository = eventRepository;
        this.driverRepository = driverRepository;
    }

    public Entry create(Long boatId, Long eventId) {
        if (entryRepository.existsByBoatIdAndEventId(boatId, eventId)) {
            throw new ConflictException("Entry already exists for boat " + boatId + " in event " + eventId);
        }
        return entryRepository.save(new Entry(boatId, eventId));
    }

    public List<Entry> findByEventId(Long eventId) {
        return entryRepository.findByEventId(eventId);
    }

    public Entry findById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));
    }

    public Entry submit(Long id) {
        Entry entry = findById(id);

        Event event = eventRepository.findById(entry.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + entry.getEventId()));
        if (event.getStatus() == EventStatus.CLOSED) {
            throw new BusinessRuleViolationException("Cannot submit entry to a closed event");
        }

        Boat boat = boatRepository.findById(entry.getBoatId())
                .orElseThrow(() -> new ResourceNotFoundException("Boat not found: " + entry.getBoatId()));
        if (boat.getOwnerId() == null) {
            throw new BusinessRuleViolationException("Cannot submit entry: boat has no owner");
        }

        if (driverRepository.countByEntryId(id) < 1) {
            throw new BusinessRuleViolationException("Cannot submit entry: no drivers assigned");
        }

        entry.setStatus(EntryStatus.SUBMITTED);
        return entryRepository.save(entry);
    }

    public Entry updateStatus(Long id, EntryStatus status) {
        Entry entry = findById(id);
        entry.setStatus(status);
        return entryRepository.save(entry);
    }

    public void delete(Long id) {
        if (!entryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entry not found: " + id);
        }
        entryRepository.deleteById(id);
    }
}
