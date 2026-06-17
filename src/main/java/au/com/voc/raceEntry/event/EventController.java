package au.com.voc.raceEntry.event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@RequestBody CreateEventRequest request) {
        Event event = new Event();
        event.setName(request.name);
        event.setDate(request.date);
        event.setLocation(request.location);
        return eventRepository.save(event);
    }

    @GetMapping
    public List<Event> listAll() {
        return eventRepository.findAll();
    }

    static class CreateEventRequest {
        public String name;
        public LocalDate date;
        public String location;
    }
}
