package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boat_class.BoatClass;
import au.com.voc.raceEntry.boat_class.BoatClassRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.utils.EncryptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EntryAssemblyService {

    private final EntryRepository entryRepository;
    private final EventRepository eventRepository;
    private final BoatRepository boatRepository;
    private final BoatClassRepository boatClassRepository;
    private final PersonRepository personRepository;
    private final EncryptionService encryptionService;

    public EntryAssemblyService(
            EntryRepository entryRepository,
            EventRepository eventRepository,
            BoatRepository boatRepository,
            BoatClassRepository boatClassRepository,
            PersonRepository personRepository,
            EncryptionService encryptionService) {
        this.entryRepository = entryRepository;
        this.eventRepository = eventRepository;
        this.boatRepository = boatRepository;
        this.boatClassRepository = boatClassRepository;
        this.personRepository = personRepository;
        this.encryptionService = encryptionService;
    }

    public Entry assemble(EntryFormData form) {
        Event event = eventRepository.getReferenceById(form.getEventId());
        Boat boat = boatRepository.getReferenceById(form.getBoatId());

        Entry entry = form.isNew()
                ? new Entry(boat, event)
                : entryRepository.getReferenceById(form.getEntryId());

        // boat classes
        List<BoatClass> boatClasses = boatClassRepository.findAllById(form.getBoatClassIds());
        entry.setBoatClasses(boatClasses);

        // drivers: clear existing then rebuild; orphanRemoval deletes removed EntryDrivers
        entry.clearDrivers();
        List<Long> driverIds = form.getDriverIds();
        for (int i = 0; i < driverIds.size(); i++) {
            Person driver = personRepository.getReferenceById(driverIds.get(i));
            DriverRole role = (i == 0) ? DriverRole.HELMSMAN : DriverRole.CREW;
            entry.addDriver(new EntryDriver(entry, driver, role));
        }

        // declarations: clear existing then rebuild; signature encrypted at rest
        entry.clearDeclarations();
        for (DeclarationFormData decl : form.getDeclarations()) {
            Person person = personRepository.getReferenceById(decl.getPersonId());
            String encryptedSignature = encryptionService.encrypt(decl.getSignature());
            entry.addDeclaration(new EntryDeclaration(entry, person, encryptedSignature, decl.getSignedDate()));
        }

        return entryRepository.save(entry);
    }
}
