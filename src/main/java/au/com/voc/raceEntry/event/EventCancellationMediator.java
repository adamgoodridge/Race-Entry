package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryRepository;
import au.com.voc.raceEntry.entry.EntryStatus;
import au.com.voc.raceEntry.notification.EntryNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventCancellationMediator {

    private final EventService eventService;
    private final EntryRepository entryRepository;
    private final EntryNotificationService notificationService;

    public EventCancellationMediator(EventService eventService,
                                     EntryRepository entryRepository,
                                     EntryNotificationService notificationService) {
        this.eventService = eventService;
        this.entryRepository = entryRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Event cancel(Long eventId) {
        Event event = eventService.cancel(eventId);
        List<Entry> entries = entryRepository.findByEventId(eventId);
        for (Entry entry : entries) {
            if (entry.getStatus() != EntryStatus.CANCELLED) {
                entry.setStatus(EntryStatus.CANCELLED);
                entryRepository.save(entry);
                notificationService.notifyCancelled(entry);
            }
        }
        return event;
    }
}
