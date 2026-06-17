package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EntryEligibilityService {

    private final EntryRepository entryRepository;
    private final EventRepository eventRepository;
    private final BoatRepository boatRepository;
    private final PersonRepository personRepository;

    public EntryEligibilityService(
            EntryRepository entryRepository,
            EventRepository eventRepository,
            BoatRepository boatRepository,
            PersonRepository personRepository) {
        this.entryRepository = entryRepository;
        this.eventRepository = eventRepository;
        this.boatRepository = boatRepository;
        this.personRepository = personRepository;
    }

    /**
     * Throws IllegalStateException listing all violations found.
     * Callers should invoke this before persisting an entry.
     */
    public void checkEligibility(EntryFormData form) {
        List<String> violations = new ArrayList<>();

        Event event = eventRepository.getReferenceById(form.getEventId());

        // 1. Event must be OPEN
        if (event.getStatus() != EventStatus.OPEN) {
            violations.add("Event '" + event.getName() + "' is not open for entries (status: " + event.getStatus() + ")");
        }

        // 2. Boat must not be soft-deleted
        Boat boat = boatRepository.getReferenceById(form.getBoatId());
        if (Boolean.TRUE.equals(boat.isDeleted())) {
            violations.add("Boat '" + boat.getName() + "' has been deleted and cannot be used for an entry");
        }

        // 3. At least one driver required
        if (form.getDriverIds().isEmpty()) {
            violations.add("At least one driver is required");
        }

        // 4. Each driver must not be soft-deleted
        for (Long driverId : form.getDriverIds()) {
            Person driver = personRepository.getReferenceById(driverId);
            if (Boolean.TRUE.equals(driver.isDeleted())) {
                violations.add("Driver '" + driver.getFirstName() + " " + driver.getLastName() + "' has been deleted and cannot be assigned");
            }
        }

        // 5. Boat-per-event uniqueness (exclude current entry on updates)
        boolean duplicate = form.isNew()
                ? entryRepository.existsByBoatAndEvent(boat, event)
                : entryRepository.existsByBoatAndEventAndIdNot(boat, event, form.getEntryId());
        if (duplicate) {
            violations.add("Boat '" + boat.getName() + "' already has an entry for event '" + event.getName() + "'");
        }

        if (!violations.isEmpty()) {
            throw new IllegalStateException("Entry is not eligible: " + String.join("; ", violations));
        }
    }
}
