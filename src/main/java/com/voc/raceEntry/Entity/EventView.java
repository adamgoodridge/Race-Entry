package com.voc.raceEntry.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class EventView {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long eventId;
    @NotNull(message = "Event's name is required.")
    private String name;
    @NotNull(message = "Event's venue is required.")
    private String venue;
    @NotNull(message = "Event's date is required.")
    private String date;
    private Integer open;
    private Integer visible;
    private Integer entriesCount;

    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        String[] fields = {"name","venue","date"};
        String[] fieldsNames = {"Name","Venue","Date"};
        for(int i = 0; i < fields.length; i++) {
            sb.append("<div class=\"mb-3\">\n");
            sb.append("<label class=\"form-label\">Event's " + fieldsNames[i] + "</label>\n");
            sb.append("<input type=\"text\" th:field=\"*{" + fields[i] + "}\" class=\"form-control\" >\n");
            sb.append("<p th:if=\"${#fields.hasErrors('" + fields[i] + "')}\" th:errorclass=\"error\" th:errors=\"*{" + fields[i] + "}\" />\n");
            sb.append("</div>\n");
        }
        System.out.println(sb);
    }

    public Integer getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(Integer entriesCount) {
        this.entriesCount = entriesCount;
    }
}
