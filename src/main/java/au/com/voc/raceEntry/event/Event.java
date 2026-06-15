//demo only
package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.utils.DateUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("unused")
@Entity
public class Event {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "event_id")
    private Long eventId;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "venue")
    private String venue;

    @Column(name = "start_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @Column(name = "duration")
    private int duration;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EntryStatus status;
    @Basic
    @Column(name = "visible")
    private Integer visible;
    @Column(name = "description")
    @Type(type = "text")
    private String description;
    @Column(name = "declaration")
    @Type(type = "text")
    private String declaration;

    @OneToMany
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(name = "event_id")
    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    @PreRemove
    public void CheckEntryBeforeRemoval() {
        if (entries.size() != 0) {
            throw new RuntimeException("You can't remove a event that has entries");
        }
    }

    public Long getEventId() {
        return eventId;
    }

    public String getDate() {
        return DateUtils.displayDays(startDate, endDate);
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public EntryStatus getStatus() {
        return status;
    }

    public void setStatus(EntryStatus status) {
        this.status = status;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

}
