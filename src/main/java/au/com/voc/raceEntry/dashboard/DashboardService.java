package au.com.voc.raceEntry.dashboard;

import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final EventRepository eventRepository;
    private final EntryRepository entryRepository;

    public DashboardService(EventRepository eventRepository, EntryRepository entryRepository) {
        this.eventRepository = eventRepository;
        this.entryRepository = entryRepository;
    }

    public AdminDashboardData getAdminDashboard() {
        List<Event> openEvents = eventRepository.findByStatusAndVisible(EventStatus.OPEN, true);
        return new AdminDashboardData(openEvents);
    }

    public UserDashboardData getUserDashboard(User user) {
        List<Event> openEvents = eventRepository.findByStatusAndVisible(EventStatus.OPEN, true);
        // User-specific entries filtered by boat owner once User→Person link is established (task #22+)
        List<Entry> entries = entryRepository.findByEvent_Status(EventStatus.OPEN);
        return new UserDashboardData(openEvents, entries);
    }
}
