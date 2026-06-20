package au.com.voc.raceEntry.event;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    protected Event() {}

    public Event(String name) {
        this.name = name;
        this.status = EventStatus.OPEN;
    }
}
