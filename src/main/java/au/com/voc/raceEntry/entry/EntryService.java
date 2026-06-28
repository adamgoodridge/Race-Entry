package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.event.EventBoatClassRepository;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.exception.BusinessRuleViolationException;
import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import au.com.voc.raceEntry.notification.EntryNotificationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final EventRepository eventRepository;
    private final EventBoatClassRepository eventBoatClassRepository;
    private final EntryNotificationService notificationService;

    public EntryService(EntryRepository entryRepository,
                        EventRepository eventRepository,
                        EventBoatClassRepository eventBoatClassRepository,
                        EntryNotificationService notificationService) {
        this.entryRepository = entryRepository;
        this.eventRepository = eventRepository;
        this.eventBoatClassRepository = eventBoatClassRepository;
        this.notificationService = notificationService;
    }

    public Entry create(Long boatId, Long eventId, Long boatClassId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));
        if (event.getStatus() != EventStatus.OPEN) {
            throw new BusinessRuleViolationException("Event is not open for entries");
        }
        if (!eventBoatClassRepository.existsByEventIdAndBoatClassId(eventId, boatClassId)) {
            throw new BusinessRuleViolationException(
                    "BoatClass " + boatClassId + " is not available for event " + eventId);
        }
        if (entryRepository.existsByBoatIdAndEventId(boatId, eventId)) {
            throw new ConflictException(
                    "Entry already exists for boat " + boatId + " in event " + eventId);
        }
        Entry entry = new Entry();
        entry.setBoatId(boatId);
        entry.setEventId(eventId);
        entry.setBoatClassId(boatClassId);
        return entryRepository.save(entry);
    }

    public List<Entry> findAll() {
        return entryRepository.findAll();
    }

    public Entry findById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));
    }

    public List<Entry> findByBoatIds(List<Long> boatIds) {
        if (boatIds.isEmpty()) return Collections.emptyList();
        return entryRepository.findByBoatIdIn(boatIds);
    }

    public Entry cancel(Long id) {
        Entry entry = findById(id);
        entry.setStatus(EntryStatus.CANCELLED);
        Entry saved = entryRepository.save(entry);
        notificationService.notifyCancelled(saved);
        return saved;
    }

    public Entry approve(Long id) {
        Entry entry = findById(id);
        if (entry.getStatus() != EntryStatus.SUBMITTED) {
            throw new BusinessRuleViolationException("Can only approve a SUBMITTED entry");
        }
        entry.setStatus(EntryStatus.APPROVED);
        Entry saved = entryRepository.save(entry);
        notificationService.notifyApproved(saved);
        return saved;
    }

    public Entry requestChanges(Long id, String comment) {
        Entry entry = findById(id);
        if (entry.getStatus() != EntryStatus.SUBMITTED) {
            throw new BusinessRuleViolationException("Can only request changes on a SUBMITTED entry");
        }
        entry.setStatus(EntryStatus.CHANGES_REQUESTED);
        entry.setSecretaryComment(comment);
        Entry saved = entryRepository.save(entry);
        notificationService.notifyChangesRequested(saved, comment);
        return saved;
    }

    public Entry markPaid(Long id) {
        Entry entry = findById(id);
        if (entry.getStatus() != EntryStatus.APPROVED) {
            throw new BusinessRuleViolationException("Can only mark an APPROVED entry as paid");
        }
        entry.setStatus(EntryStatus.PAID);
        return entryRepository.save(entry);
    }
}
