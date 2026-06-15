//demo only
package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.utils.DateUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class EventView {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long eventId;
    @NotNull(message = "Event's name is required.")
    private String name;
    @NotNull(message = "Event's venue is required.")
    private String venue;
    private LocalDate startDate;
    private int duration;
    private Integer open;
    private Integer visible;
    private Integer entriesCount;

    public Long getEventId() {
        return eventId;
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


    public String getDate() {
        return DateUtils.displayDays(startDate, duration);
    }

    public Integer getOpen() {
        return open;
    }

    public Integer getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(Integer entriesCount) {
        this.entriesCount = entriesCount;
    }
}
