package au.com.voc.raceEntry.entry;

import javax.persistence.*;

@Entity
@Table(name = "entry")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    @Column(nullable = false)
    private Long boatId;

    @Column(nullable = false)
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status;

    protected Entry() {}

    public Entry(Long boatId, Long eventId) {
        this.boatId = boatId;
        this.eventId = eventId;
        this.status = EntryStatus.PENDING;
    }

    public Long getEntryId() { return entryId; }
    public Long getBoatId() { return boatId; }
    public Long getEventId() { return eventId; }
    public EntryStatus getStatus() { return status; }
    public void setStatus(EntryStatus status) { this.status = status; }
}
