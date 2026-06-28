package au.com.voc.raceEntry.declaration;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryDriver;
import au.com.voc.raceEntry.entry.EntryDriverRepository;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class DeclarationFormService {

    private final EntryService entryService;
    private final BoatRepository boatRepository;
    private final EventRepository eventRepository;
    private final BoatClassRepository boatClassRepository;
    private final EntryDriverRepository entryDriverRepository;
    private final PersonRepository personRepository;

    public DeclarationFormService(EntryService entryService,
                                  BoatRepository boatRepository,
                                  EventRepository eventRepository,
                                  BoatClassRepository boatClassRepository,
                                  EntryDriverRepository entryDriverRepository,
                                  PersonRepository personRepository) {
        this.entryService = entryService;
        this.boatRepository = boatRepository;
        this.eventRepository = eventRepository;
        this.boatClassRepository = boatClassRepository;
        this.entryDriverRepository = entryDriverRepository;
        this.personRepository = personRepository;
    }

    public byte[] generate(Long entryId) {
        Entry entry = entryService.findById(entryId);

        Boat boat = boatRepository.findById(entry.getBoatId())
                .orElseThrow(() -> new ResourceNotFoundException("Boat not found: " + entry.getBoatId()));
        Event event = eventRepository.findById(entry.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + entry.getEventId()));
        BoatClass boatClass = boatClassRepository.findById(entry.getBoatClassId())
                .orElseThrow(() -> new ResourceNotFoundException("BoatClass not found: " + entry.getBoatClassId()));

        List<EntryDriver> drivers = entryDriverRepository.findByEntryId(entryId);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            doc.add(new Paragraph("Declaration Form", titleFont));
            doc.add(new Paragraph("Victoria Outboard Club", bodyFont));
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Event", headingFont));
            doc.add(new Paragraph("Name: " + event.getName(), bodyFont));
            doc.add(new Paragraph("Start Date: " + event.getStartDate(), bodyFont));
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Boat", headingFont));
            doc.add(new Paragraph("Name: " + boat.getName(), bodyFont));
            doc.add(new Paragraph("Race Number: " + boat.getRaceNumber(), bodyFont));
            doc.add(new Paragraph("Class: " + boatClass.getName(), bodyFont));
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Drivers", headingFont));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell(new PdfPCell(new Phrase("Name", headingFont)));
            table.addCell(new PdfPCell(new Phrase("SBA Licence", headingFont)));
            table.addCell(new PdfPCell(new Phrase("SBA Expiry", headingFont)));

            for (EntryDriver ed : drivers) {
                Person person = personRepository.findById(ed.getPersonId()).orElse(null);
                if (person != null) {
                    table.addCell(person.getFirstName() + " " + person.getLastName());
                    table.addCell(person.getSbaLicenceNumber() != null ? person.getSbaLicenceNumber() : "");
                    table.addCell(person.getSbaExpiryDate() != null ? person.getSbaExpiryDate().toString() : "");
                }
            }

            doc.add(table);
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Signature: ______________________________", bodyFont));
            doc.add(new Paragraph("Date: _______________", bodyFont));

            doc.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate declaration PDF", e);
        }
    }
}
