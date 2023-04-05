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

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    //Generates code for thymeleaf
    public static void main(String[] args) {
        String[] fields = {"name", "venue", "date"};
        //Names that will appear in the web based interface
        String[] fieldsNames = {"Name", "Venue", "Date"};
        for (int i = 0; i < fields.length; i++) {
            System.out.println("<div class=\"mb-3\">\n");
            System.out.println("<label class=\"form-label\">Event's " + fieldsNames[i] + "</label>\n");
            System.out.println("<input type=\"text\" th:field=\"*{" + fields[i] + "}\" class=\"form-control\" >\n");
            System.out.println("<p th:if=\"${#fields.hasErrors('" + fields[i] + "')}\" th:errorclass=\"error\" th:errors=\"*{" + fields[i] + "}\" />\n");
            System.out.println("</div>\n");
        }
    }

    public Integer getEntriesCount() {
        return entriesCount;
    }

    public void setEntriesCount(Integer entriesCount) {
        this.entriesCount = entriesCount;
    }
}
