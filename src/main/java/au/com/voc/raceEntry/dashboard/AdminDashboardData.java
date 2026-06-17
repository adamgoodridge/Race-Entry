package au.com.voc.raceEntry.dashboard;

import au.com.voc.raceEntry.event.Event;

import java.util.List;

public class AdminDashboardData {

    private final List<Event> openEvents;

    public AdminDashboardData(List<Event> openEvents) {
        this.openEvents = openEvents;
    }

    public List<Event> getOpenEvents() { return openEvents; }
}
