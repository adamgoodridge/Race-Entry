package com.voc.raceEntry.Entity;

import javax.persistence.*;
import java.util.List;

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
    @Basic
    @Column(name = "date")
    private String date;
    @Basic
    @Column(name = "open")
    private Integer open;
    @Basic
    @Column(name = "visible")
    private Integer visible;

    @OneToMany(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "event_id")
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
            /*
            sb.append("<div class=\"mb-3\">");
            sb.append("\n<label for=\"" +fields[i] + "\" class=\"form-label\">Enter your "+ fieldsNames[i] + "</label>");
            sb.append("\n<form:input path=\""+ fields[i] +"\" id=\""+ fields[i] +"\" class=\"form-control\" /><form:errors path=\""
                    + fields[i] +"\" cssClass=\"error\">");
            sb.append("\n</div>");

             */
            sb.append("<div class=\"mb-3\">\n");
            sb.append("<label class=\"form-label\">Event's "+ fieldsNames[i] +"</label>\n");
            sb.append("<input type=\"text\" th:field=\"*{"+fields[i]+"}\" class=\"form-control\" >\n");
            sb.append("<p th:if=\"${#fields.hasErrors('"+fields[i]+"\')}\" th:errorclass=\"error\" th:errors=\"*{"+fields[i]+"}\" />\n");
            sb.append("</div>\n");
        }
        System.out.println(sb.toString());
    }
}
