package au.com.voc.raceEntry.dashboard;

import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.event.Event;

import java.util.List;

public class UserDashboardData {

    private final List<Event> openEvents;
    private final List<Entry> userEntries;

    public UserDashboardData(List<Event> openEvents, List<Entry> userEntries) {
        this.openEvents = openEvents;
        this.userEntries = userEntries;
    }

    public List<Event> getOpenEvents() { return openEvents; }
    public List<Entry> getUserEntries() { return userEntries; }
}
