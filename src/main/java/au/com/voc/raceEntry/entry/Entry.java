package au.com.voc.raceEntry.entry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "entry", uniqueConstraints = @UniqueConstraint(columnNames = {"boat_id", "event_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "boat_id", nullable = false)
    private Long boatId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "boat_class_id", nullable = false)
    private Long boatClassId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status = EntryStatus.DRAFT;

    private String secretaryComment;
}
