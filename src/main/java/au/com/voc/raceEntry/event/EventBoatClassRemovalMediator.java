package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryRepository;
import au.com.voc.raceEntry.entry.EntryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
public class EventBoatClassRemovalMediator {

    private static final Set<EntryStatus> CANCELLABLE = EnumSet.of(
            EntryStatus.DRAFT, EntryStatus.SUBMITTED,
            EntryStatus.CHANGES_REQUESTED, EntryStatus.APPROVED);

    private final EventService eventService;
    private final EntryRepository entryRepository;

    public EventBoatClassRemovalMediator(EventService eventService, EntryRepository entryRepository) {
        this.eventService = eventService;
        this.entryRepository = entryRepository;
    }

    @Transactional
    public void removeBoatClass(Long eventId, Long boatClassId) {
        eventService.removeBoatClass(eventId, boatClassId);
        List<Entry> entries = entryRepository.findByEventId(eventId);
        for (Entry entry : entries) {
            if (boatClassId.equals(entry.getBoatClassId()) && CANCELLABLE.contains(entry.getStatus())) {
                entry.setStatus(EntryStatus.CANCELLED);
                entryRepository.save(entry);
            }
        }
    }
}
