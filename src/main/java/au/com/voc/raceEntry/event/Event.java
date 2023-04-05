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
    @Column(name = "open", nullable = false, columnDefinition = "BIT", length = 1)
    private Boolean open;
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

    private String previousUrl;

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

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
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

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        String[] fields = {"name", "venue", "date"};
        String[] fieldsNames = {"Name", "Venue", "Date"};
        for (int i = 0; i < fields.length; i++) {
            /*
            sb.append("<div class=\"mb-3\">");
            sb.append("\n<label for=\"" +fields[i] + "\" class=\"form-label\">Enter your "+ fieldsNames[i] + "</label>");
            sb.append("\n<form:input path=\""+ fields[i] +"\" id=\""+ fields[i] +"\" class=\"form-control\" /><form:errors path=\""
                    + fields[i] +"\" cssClass=\"error\">");
            sb.append("\n</div>");

             */
            sb.append("<div class=\"mb-3\">\n");
            sb.append("<label class=\"form-label\">Event's " + fieldsNames[i] + "</label>\n");
            sb.append("<input type=\"text\" th:field=\"*{" + fields[i] + "}\" class=\"form-control\" >\n");
            sb.append("<p th:if=\"${#fields.hasErrors('" + fields[i] + "')}\" th:errorclass=\"error\" th:errors=\"*{" + fields[i] + "}\" />\n");
            sb.append("</div>\n");
        }
        System.out.println(sb);
    }
}
