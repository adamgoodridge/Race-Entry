package au.com.voc.raceEntry.entry;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class EntryFormData {

    private Long entryId;

    @NotNull
    private Long eventId;

    @NotNull
    private Long boatId;

    @Size(min = 1, message = "At least one driver is required")
    private List<Long> driverIds = new ArrayList<>();

    private List<Long> boatClassIds = new ArrayList<>();

    @Size(min = 1, message = "At least one declaration is required")
    private List<DeclarationFormData> declarations = new ArrayList<>();

    public EntryFormData() {}

    public Long getEntryId() { return entryId; }
    public Long getEventId() { return eventId; }
    public Long getBoatId() { return boatId; }
    public List<Long> getDriverIds() { return driverIds; }
    public List<Long> getBoatClassIds() { return boatClassIds; }
    public List<DeclarationFormData> getDeclarations() { return declarations; }

    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public void setBoatId(Long boatId) { this.boatId = boatId; }
    public void setDriverIds(List<Long> driverIds) { this.driverIds = driverIds; }
    public void setBoatClassIds(List<Long> boatClassIds) { this.boatClassIds = boatClassIds; }
    public void setDeclarations(List<DeclarationFormData> declarations) { this.declarations = declarations; }

    public boolean isNew() { return entryId == null; }
}
