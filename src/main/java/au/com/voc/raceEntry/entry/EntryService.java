//demo only
package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventService;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.utils.DateUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class EntryService {
    private final EntryRepository entryRepository;
    private final PersonRepository personRepository;

    private final BoatRepository boatRepository;

    private final EventService eventService;

    @Autowired
    public EntryService(EntryRepository entryRepository, PersonRepository personRepository, BoatRepository boatRepository, EventService eventService) {
        this.entryRepository = entryRepository;
        this.personRepository = personRepository;
        this.boatRepository = boatRepository;
        this.eventService = eventService;
    }


    public Entry formEventEntry(EntryFormData entryFormData) {
        //isn't getting event
        //conversion done as access to all different repositories
        Entry entry = new Entry();
        //get all the many-to-one relationships & set
        if (entryFormData.getEntryId() != -1) {
            entry.setEntryId(entryFormData.getEntryId());
        }
        Event event = eventService.getEvent(entryFormData.getEventId());
        entry.setEvent(event);
        if (entryFormData.getBoatId() != 0) {
            Boat boat = boatRepository.getReferenceById(entryFormData.getBoatId());
            entry.setBoat(boat);
        }
        if (entryFormData.getFirstDriverId() != 0) {
            Person driver = personRepository.getReferenceById(entryFormData.getFirstDriverId());
            entry.setPersonOne(driver);
        }
        entry.setUser(entryFormData.getUser());
        entry.setBoatClass(entryFormData.getBoatClass());

        entry.setDeclarationOwnerSignature(entryFormData.getDeclarationOwnerSignature());
        entry.setDeclarationOwnerDate(entryFormData.getDeclarationOwnerDate());

        entry.setDeclarationDriverOneSignature(entryFormData.getDeclarationDriverOneSignature());
        entry.setDeclarationDriverOneDate(entryFormData.getDeclarationDriverOneDate());

        entry.setDeclarationDriverTwoSignature(entryFormData.getDeclarationDriverTwoSignature());
        entry.setDeclarationDriverTwoDate(entryFormData.getDeclarationDriverTwoDate());
        //if it is a second driver
        if (entryFormData.getSecondDriverId() != 0) {
            Person secondDriver = personRepository.getReferenceById(entryFormData.getSecondDriverId());
            entry.setPersonTwo(secondDriver);
        }
        return entry;
    }

    public void updateEntry(Entry entry) {
        entryRepository.save(entry);
    }

    public List<Entry> getEntriesByBoatOpened(long boatId) {
        return entryRepository.entriesByBoatOpened(boatId);
    }

    public Entry getEntry(Long id) {
        return entryRepository.getReferenceById(id);
    }

    public List<Entry> getEntriesByEvent(Long eventId) {
        return entryRepository.entriesByEvent(eventId);
    }

    public List<Entry> getEntriesByUser(User user) {
        return entryRepository.entriesOpenByUser(user);
    }

    public List<Entry> getOpenEntriesByPerson(Long driverId) {
        return entryRepository.existsOpenEntriesByPerson(driverId);
    }

    public byte[] generatePDF(Entry entry) throws IOException {
        //https://stackoverflow.com/questions/59346914/how-to-read-pdf-from-the-jar-file
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("pdfs/APBA_Fillable.pdf");
        //String path = Thread.currentThread().getContextClassLoader().getResource("form.pdf").getFile();
        PDDocument pdfDocument = PDDocument.load(inputStream);
        PDDocumentCatalog documentCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = documentCatalog.getAcroForm();
        if (acroForm != null) {
            //because event is loaded lazy load by default
            if (entry.getEvent() == null) {
                Event event = eventService.getEvent(entry.getEvent().getEventId());
                entry.setEvent(event);
            }
            Boat boat = boatRepository.getReferenceById(entry.getBoat().getBoatId());
            entry.setBoat(boat);
            Person owner = personRepository.getReferenceById(entry.getBoat().getOwner().getPersonId());
            entry.getBoat().setOwner(owner);
            //event info
            acroForm.getField("eventName").setValue(entry.getEvent().getName());
            acroForm.getField("className").setValue(entry.getBoatClass());
            ///acroForm.getField("eventDate").setValue(entry.getEvent().getDate);
            acroForm.getField("venueName").setValue(entry.getEvent().getVenue());
            //boat info
            acroForm.getField("boatRaceNo").setValue(entry.getBoat().getRaceNo());
            acroForm.getField("boatName").setValue(entry.getBoat().getName());
            acroForm.getField("boatSBANo").setValue(entry.getBoat().getSbaRegistrationNo());
            String sbaRegistrationDate = DateUtils.format(entry.getBoat().getSbaLicenceExpiryDate());
            acroForm.getField("boatSBADate").setValue(sbaRegistrationDate);
            //driver info
            acroForm.getField("ownerName").setValue(entry.getBoat().getOwner().getFullName());
            acroForm.getField("ownerClub").setValue(entry.getBoat().getOwner().getClubName());
            acroForm.getField("ownerAddress").setValue(entry.getBoat().getOwner().getFullAddress());
            acroForm.getField("ownerLicenceNO").setValue(entry.getBoat().getOwner().getSbaLicence());
            acroForm.getField("ownerPhoneNO").setValue(entry.getBoat().getOwner().getPhoneNo());

            //PDSignatureField pdSignatureField = new PDSignatureField(acroForm);
            acroForm.getField("boatMotorCapacity").setValue(entry.getBoat().getCapacityOfMotor());
            acroForm.getField("boatBeam").setValue(entry.getBoat().getBeam().toString());
            acroForm.getField("boatLength").setValue(entry.getBoat().getLength().toString());
            acroForm.getField("boatClass").setValue(entry.getBoat().getClassName());
            //driver 1 info
            acroForm.getField("driver1Name").setValue(entry.getPersonOne().getFullName());
            acroForm.getField("driver1Club").setValue(entry.getPersonOne().getClubName());
            acroForm.getField("driver1SBADate").setValue(DateUtils.format(entry.getPersonOne().getSbaLicenceExpiryDate()));
            acroForm.getField("driver1SBANo").setValue(entry.getPersonOne().getSbaLicence());
            acroForm.getField("driver1APBANo").setValue(entry.getPersonOne().getApbaLicence().toString());
            acroForm.getField("driver1ContactNumber").setValue(entry.getPersonOne().getPhoneNo());
            if (entry.getPersonTwo() != null) {
                acroForm.getField("driver2Club").setValue(entry.getPersonTwo().getClubName());
                acroForm.getField("driver2ContactNumber").setValue(entry.getPersonTwo().getPhoneNo());
                acroForm.getField("driver2Name").setValue(entry.getPersonTwo().getFullName());
                acroForm.getField("driver2SBANo").setValue(entry.getPersonTwo().getSbaLicence());
                acroForm.getField("driver2APBAANo").setValue(entry.getPersonTwo().getApbaLicence().toString());
                acroForm.getField("driver2SBADate").setValue(DateUtils.format(entry.getPersonTwo().getSbaLicenceExpiryDate()));
            }


        }
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        pdfDocument.save(bais);
        pdfDocument.close();
        return bais.toByteArray();
    }

}
