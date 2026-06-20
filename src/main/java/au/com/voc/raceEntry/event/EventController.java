package au.com.voc.raceEntry.event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@RequestBody EventRequest request) {
        return service.create(request.getName());
    }

    @GetMapping
    public List<Event> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Event getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody EventRequest request) {
        return service.update(id, request.getName());
    }

    @PutMapping("/{id}/close")
    public Event close(@PathVariable Long id) {
        return service.close(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    static class EventRequest {
        private String name;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
