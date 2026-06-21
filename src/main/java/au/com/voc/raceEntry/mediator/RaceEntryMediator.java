package au.com.voc.raceEntry.mediator;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryDriverService;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.entry.EntryStatus;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventService;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Service;

@Service
public class RaceEntryMediator {

    private final EntryService entryService;
    private final BoatService boatService;
    private final EventService eventService;
    private final EntryDriverService entryDriverService;

    public RaceEntryMediator(EntryService entryService, BoatService boatService,
                             EventService eventService, EntryDriverService entryDriverService) {
        this.entryService = entryService;
        this.boatService = boatService;
        this.eventService = eventService;
        this.entryDriverService = entryDriverService;
    }

    public Entry submit(Long entryId) {
        Entry entry = entryService.findById(entryId);

        Event event = eventService.findById(entry.getEventId());
        if (event.getStatus() == EventStatus.CLOSED) {
            throw new BusinessRuleViolationException("Cannot submit entry to a closed event");
        }

        Boat boat = boatService.findById(entry.getBoatId());
        if (boat.getOwnerId() == null) {
            throw new BusinessRuleViolationException("Cannot submit entry: boat has no owner");
        }

        if (entryDriverService.countByEntryId(entryId) < 1) {
            throw new BusinessRuleViolationException("Cannot submit entry: no drivers assigned");
        }

        return entryService.updateStatus(entryId, EntryStatus.SUBMITTED);
    }
}
