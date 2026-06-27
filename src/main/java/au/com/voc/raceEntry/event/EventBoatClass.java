package au.com.voc.raceEntry.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "event_boat_class",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "boat_class_id"}))
@Getter
@Setter
@NoArgsConstructor
public class EventBoatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "boat_class_id", nullable = false)
    private Long boatClassId;
}
