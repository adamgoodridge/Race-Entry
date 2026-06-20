package au.com.voc.raceEntry.entry;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "entry", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"boat_id", "event_id"})
})
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "boat_id", nullable = false)
    private Long boatId;

    @Setter
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status;

    protected Entry() {}

    public Entry(Long boatId, Long eventId) {
        this.boatId = boatId;
        this.eventId = eventId;
        this.status = EntryStatus.DRAFT;
    }
}
