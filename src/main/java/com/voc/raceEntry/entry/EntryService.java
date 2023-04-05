package com.voc.raceEntry.entry;

import com.voc.raceEntry.boat.Boat;
import com.voc.raceEntry.boat.BoatService;
import com.voc.raceEntry.driver.Driver;
import com.voc.raceEntry.driver.DriverService;
import com.voc.raceEntry.event.Event;
import com.voc.raceEntry.event.EventService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class EntryService {
    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private DriverService driverService;
    @Autowired
    private BoatService boatService;
    @Autowired
    private EventService eventService;

    public void updateEntry(EntryFormData entryFormData) {
        Entry entry = new Entry();
        //get all the many to one relationships & set
        Event event = eventService.getEvent(entryFormData.getEventId());
        entry.setEvent(event);
        Boat boat = boatService.getBoat(entryFormData.getBoatId());
        entry.setBoat(boat);
        Driver driver = driverService.getDriver(entryFormData.getFirstDriverId());
        System.out.println("=\n\n\n"+driver+"=\n\n\n");
        entry.setDriver_one(driver);
        System.out.println("=\n\n\n"+entry.getEvent().getEventId()+"=\n\n\n");
        //if it is a second driver
        if(entryFormData.getSecondDriverId() != 0) {
            Driver secondDriver = driverService.getDriver(entryFormData.getSecondDriverId());
            entry.setDriver_two(secondDriver);
        }
        entryRepository.save(entry);
    }

    public Entry getEntry(Long id) {
        return entryRepository.getReferenceById(id);
    }

    public List<Entry> getEntriesByEvent(Long eventId) {
        return entryRepository.entriesByEvent(eventId);
    }


    public byte[] generatePDF(Entry entry) throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("APBA_Fillable.pdf").getFile();
        //String path = Thread.currentThread().getContextClassLoader().getResource("form.pdf").getFile();
        System.out.println(path);
        PDDocument pdfDocument = PDDocument.load(new File(path));
        PDDocumentCatalog documentCatalog = pdfDocument.getDocumentCatalog();
        PDAcroForm acroForm = documentCatalog.getAcroForm();
        if(acroForm != null) {
            //because event is loaded lazy load by default
            if(entry.getEvent() == null) {
               Event event = eventService.getEvent(entry.getEvent().getEventId());
               entry.setEvent(event);
            }
            Boat boat = boatService.getBoat(entry.getBoat().getBoatId());
            entry.setBoat(boat);
            Driver owner = driverService.getDriver(entry.getBoat().getOwner().getDriverId());
            entry.getBoat().setOwner(owner);
            acroForm.getField("eventName").setValue(entry.getEvent().getName());
            acroForm.getField("eventDate").setValue(entry.getEvent().getDate());
            acroForm.getField("venueName").setValue(entry.getEvent().getVenue());
            acroForm.getField("boatRaceNo").setValue(entry.getBoat().getRaceNo());
            acroForm.getField("boatName").setValue(entry.getBoat().getName());
            ///acroForm.getField("boatSBANo").setValue(entry.getBoat().);
            ///acroForm.getField("boatSBADate").setValue();

            acroForm.getField("ownerName").setValue(entry.getBoat().getOwner().getFullName());
            //acroForm.getField("ownerClub").setValue();
            acroForm.getField("ownerAddress").setValue(entry.getBoat().getOwner().getFullAddress());
            ///acroForm.getField("ownerLicenceNO").setValue(entry.getBoat().getOwner().);
            acroForm.getField("ownerPhoneNO").setValue(entry.getBoat().getOwner().getPhoneNo());

            PDSignatureField pdSignatureField = new PDSignatureField(acroForm);
            acroForm.getField("boatMotorCapacity").setValue(entry.getBoat().getCapacityOfMotor());
            acroForm.getField("boatBeam").setValue(entry.getBoat().getBeam().toString());
            acroForm.getField("boatLength").setValue(entry.getBoat().getLength().toString());
            //acroForm.getField("boatClass").setValue();
            acroForm.getField("driver1Name").setValue(entry.getDriver_one().getFullName());
            ////acroForm.getField("driver1Club").setValue();
            acroForm.getField("driver1SBADate").setValue(entry.getDriver_one().getsbaLicenceExpiry());
            //acroForm.getField("driver1SBANo").setValue();
            //acroForm.getField("driver1APBANo").setValue(entry.getDriver_one().get);
            acroForm.getField("driver1ContactNumber").setValue(entry.getDriver_one().getPhoneNo());
            if(entry.getDriver_two() != null) {
                /*acroForm.getField("driver2Club").setValue();
                acroForm.getField("driver2ContactNumber").setValue(entry.getDriver_two().getPhoneNo());
                acroForm.getField("driver2Name").setValue(entry.getDriver_two().getFullName());
                //acroForm.getField("driver2SBANo").setValue(entry.getDriver_two().getSbaLicence());
                //acroForm.getField("driver2APBAANo").setValue();
                acroForm.getField("driver2SBADate").setValue(entry.getDriver_two().getSbaDate());


                 */
            }


        }
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        pdfDocument.save(bais);
        byte[] contents = bais.toByteArray();
        return contents;
    }

}
