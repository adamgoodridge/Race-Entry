package au.com.voc.raceEntry.entry;

import javax.persistence.*;

@Entity
@Table(name = "entry")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    private Long boatId;
    private Long eventId;

    @Enumerated(EnumType.STRING)
    private EntryStatus status;

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public Long getBoatId() { return boatId; }
    public void setBoatId(Long boatId) { this.boatId = boatId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public EntryStatus getStatus() { return status; }
    public void setStatus(EntryStatus status) { this.status = status; }
}
