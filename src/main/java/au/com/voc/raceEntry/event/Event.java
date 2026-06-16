package au.com.voc.raceEntry.event;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(nullable = false)
    private Boolean visible;

    protected Event() {}

    public Event(String name, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = EventStatus.OPEN;
        this.visible = true;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public long getDuration() { return ChronoUnit.DAYS.between(startDate, endDate); }
    public EventStatus getStatus() { return status; }
    public Boolean getVisible() { return visible; }

    public void setName(String name) { this.name = name; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(EventStatus status) { this.status = status; }
    public void setVisible(Boolean visible) { this.visible = visible; }

    public boolean isOpen() { return status == EventStatus.OPEN; }
}
