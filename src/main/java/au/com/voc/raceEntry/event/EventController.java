package au.com.voc.raceEntry.event;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventCancellationMediator cancellationMediator;
    private final EventBoatClassRemovalMediator boatClassRemovalMediator;

    public EventController(EventService eventService,
                           EventCancellationMediator cancellationMediator,
                           EventBoatClassRemovalMediator boatClassRemovalMediator) {
        this.eventService = eventService;
        this.cancellationMediator = cancellationMediator;
        this.boatClassRemovalMediator = boatClassRemovalMediator;
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @GetMapping
    public List<Event> findAll() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public Event findById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PutMapping("/{id}")
    public Event update(@PathVariable Long id, @RequestBody EventRequest request) {
        return eventService.update(id, request);
    }

    @PostMapping("/{id}/close")
    public Event close(@PathVariable Long id) {
        return eventService.close(id);
    }

    @PostMapping("/{id}/cancel")
    public Event cancel(@PathVariable Long id) {
        return cancellationMediator.cancel(id);
    }

    @PostMapping("/{id}/boat-classes/{classId}")
    public ResponseEntity<EventBoatClass> addBoatClass(@PathVariable Long id, @PathVariable Long classId) {
        return ResponseEntity.ok(eventService.addBoatClass(id, classId));
    }

    @DeleteMapping("/{id}/boat-classes/{classId}")
    public ResponseEntity<Void> removeBoatClass(@PathVariable Long id, @PathVariable Long classId) {
        boatClassRemovalMediator.removeBoatClass(id, classId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/boat-classes")
    public List<EventBoatClass> listBoatClasses(@PathVariable Long id) {
        return eventService.findBoatClasses(id);
    }
}
