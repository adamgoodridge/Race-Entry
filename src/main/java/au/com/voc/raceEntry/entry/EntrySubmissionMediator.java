package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.exception.BusinessRuleViolationException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntrySubmissionMediator {

    private final EntryRepository entryRepository;
    private final EntryDriverRepository entryDriverRepository;
    private final EventRepository eventRepository;
    private final BoatRepository boatRepository;
    private final PersonRepository personRepository;

    public EntrySubmissionMediator(EntryRepository entryRepository,
                                   EntryDriverRepository entryDriverRepository,
                                   EventRepository eventRepository,
                                   BoatRepository boatRepository,
                                   PersonRepository personRepository) {
        this.entryRepository = entryRepository;
        this.entryDriverRepository = entryDriverRepository;
        this.eventRepository = eventRepository;
        this.boatRepository = boatRepository;
        this.personRepository = personRepository;
    }

    public Entry submit(Long entryId) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + entryId));

        Event event = eventRepository.findById(entry.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + entry.getEventId()));

        if (event.getStatus() != EventStatus.OPEN) {
            throw new BusinessRuleViolationException("Cannot submit: event is not open");
        }

        var boat = boatRepository.findById(entry.getBoatId())
                .orElseThrow(() -> new ResourceNotFoundException("Boat not found: " + entry.getBoatId()));

        if (boat.getOwnerId() == null) {
            throw new BusinessRuleViolationException("Cannot submit: boat has no owner");
        }

        List<EntryDriver> drivers = entryDriverRepository.findByEntryId(entryId);

        if (drivers.isEmpty()) {
            throw new BusinessRuleViolationException("Cannot submit: entry has no drivers");
        }

        if (event.getMaxDrivers() != null && drivers.size() > event.getMaxDrivers()) {
            throw new BusinessRuleViolationException(
                    "Cannot submit: exceeds maximum driver limit of " + event.getMaxDrivers());
        }

        for (EntryDriver driver : drivers) {
            Person person = personRepository.findById(driver.getPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Person not found: " + driver.getPersonId()));
            if (person.getSbaExpiryDate() == null
                    || person.getSbaExpiryDate().isBefore(event.getStartDate())) {
                throw new BusinessRuleViolationException(
                        "Driver " + person.getFirstName() + " " + person.getLastName()
                                + " has an invalid or expired SBA licence");
            }
        }

        entry.setStatus(EntryStatus.SUBMITTED);
        return entryRepository.save(entry);
    }
}
