package au.com.voc.raceEntry.mediator;

import au.com.voc.raceEntry.entry.DriverRequest;
import au.com.voc.raceEntry.entry.EntryDriver;
import au.com.voc.raceEntry.entry.EntryDriverService;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.person.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryDriverMediator {

    private final EntryDriverService entryDriverService;
    private final EntryService entryService;
    private final PersonService personService;

    public EntryDriverMediator(EntryDriverService entryDriverService, EntryService entryService,
                               PersonService personService) {
        this.entryDriverService = entryDriverService;
        this.entryService = entryService;
        this.personService = personService;
    }

    public EntryDriver addDriver(Long entryId, DriverRequest request) {
        entryService.findById(entryId);
        personService.findById(request.getPersonId());
        return entryDriverService.addDriver(entryId, request);
    }

    public List<EntryDriver> findByEntryId(Long entryId) {
        return entryDriverService.findByEntryId(entryId);
    }

    public EntryDriver updateRole(Long entryDriverId, String role) {
        return entryDriverService.updateRole(entryDriverId, role);
    }

    public void removeDriver(Long entryDriverId) {
        entryDriverService.removeDriver(entryDriverId);
    }
}
