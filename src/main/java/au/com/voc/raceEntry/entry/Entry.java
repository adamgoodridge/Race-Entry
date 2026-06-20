package au.com.voc.raceEntry.entry;

import javax.persistence.*;

@Entity
@Table(name = "entry", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"boat_id", "event_id"})
})
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "boat_id", nullable = false)
    private Long boatId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status;

    protected Entry() {}

    public Entry(Long boatId, Long eventId) {
        this.boatId = boatId;
        this.eventId = eventId;
        this.status = EntryStatus.DRAFT;
    }

    public Long getId() { return id; }

    public Long getBoatId() { return boatId; }
    public void setBoatId(Long boatId) { this.boatId = boatId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public EntryStatus getStatus() { return status; }
    public void setStatus(EntryStatus status) { this.status = status; }
}
