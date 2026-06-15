package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.utils.DateUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class EventFormData {

    private Long eventId;
    @NotNull(message = "Event's name is required.")
    private String name;
    @NotNull(message = "Event's venue is required.")
    private String venue;
    private LocalDate startDate;
    private int duration;
    private EntryStatus status;
    private Integer visible;
    private Long entriesCount;

    public EventFormData() {
    }

    public EventFormData(Long eventId, String name, String venue, LocalDate startDate,
                         int duration, EntryStatus status, Integer visible, Long entriesCount) {
        this.eventId = eventId;
        this.name = name;
        this.venue = venue;
        this.startDate = startDate;
        this.duration = duration;
        this.status = status;
        this.visible = visible;
        this.entriesCount = entriesCount;
    }

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

    public Long getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(Long entriesCount) {
        this.entriesCount = entriesCount;
    }

    public String getDate() {
        return DateUtils.displayDays(startDate, duration);
    }
}
