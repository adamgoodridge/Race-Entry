package au.com.voc.raceEntry.event;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoCloseScheduler {

    private final EventService eventService;

    public AutoCloseScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(fixedDelay = 60_000)
    public void closeExpiredEvents() {
        eventService.closeExpiredEvents();
    }
}
