package au.com.voc.raceEntry.event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    public Event create(@Valid @RequestBody CreateEventRequest request) {
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
        @NotBlank(message = "name is required")
        public String name;
        @NotNull(message = "date is required")
        public LocalDate date;
        @NotBlank(message = "location is required")
        public String location;
    }
}
