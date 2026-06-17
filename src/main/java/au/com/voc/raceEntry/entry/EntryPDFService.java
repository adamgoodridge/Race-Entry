package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.person.Person;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class EntryPDFService {

    public byte[] generatePDF(Entry entry) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("APBA_Fillable.pdf");
        try (PDDocument pdfDocument = PDDocument.load(inputStream)) {
            PDDocumentCatalog catalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = catalog.getAcroForm();
            if (acroForm != null) {
                // entry.getEvent() is @ManyToOne(optional=false) — never null
                fillEventFields(acroForm, entry.getEvent());
                fillBoatFields(acroForm, entry.getBoat());
                fillDriverFields(acroForm, entry.getDrivers());
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pdfDocument.save(out);
            return out.toByteArray();
        }
    }

    private void fillEventFields(PDAcroForm form, Event event) throws IOException {
        setField(form, "eventName", event.getName());
        setField(form, "eventStartDate", event.getStartDate().toString());
        setField(form, "eventEndDate", event.getEndDate().toString());
    }

    private void fillBoatFields(PDAcroForm form, Boat boat) throws IOException {
        setField(form, "boatName", boat.getName());
        setField(form, "boatSailNumber", boat.getSailNumber());
        setField(form, "boatClass", boat.getBoatClass().getClassName());
        // owner is nullable: a boat can exist without an owner (validated at entry submission)
        if (boat.getOwner() != null) {
            Person owner = boat.getOwner();
            setField(form, "ownerName", owner.getFirstName() + " " + owner.getLastName());
            setField(form, "ownerEmail", owner.getEmail());
        }
    }

    private void fillDriverFields(PDAcroForm form, List<EntryDriver> drivers) throws IOException {
        for (int i = 0; i < drivers.size(); i++) {
            Person driver = drivers.get(i).getDriver();
            String prefix = "driver" + (i + 1);
            setField(form, prefix + "Name", driver.getFirstName() + " " + driver.getLastName());
            setField(form, prefix + "Email", driver.getEmail());
            setField(form, prefix + "Role", drivers.get(i).getRole().name());
        }
    }

    // Null-safe: silently skips fields that don't exist in the PDF form.
    private void setField(PDAcroForm form, String fieldName, String value) throws IOException {
        PDField field = form.getField(fieldName);
        if (field != null) {
            field.setValue(value != null ? value : "");
        }
    }
}
