package au.com.voc.raceEntry.event;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private Boolean visible;

    protected Event() {}

    public Event(String name, LocalDate eventDate) {
        this.name = name;
        this.eventDate = eventDate;
        this.status = EventStatus.OPEN;
        this.visible = true;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getEventDate() { return eventDate; }
    public EventStatus getStatus() { return status; }
    public Boolean getVisible() { return visible; }

    public void setName(String name) { this.name = name; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public void setStatus(EventStatus status) { this.status = status; }
    public void setVisible(Boolean visible) { this.visible = visible; }

    public boolean isOpen() { return status == EventStatus.OPEN; }
}
