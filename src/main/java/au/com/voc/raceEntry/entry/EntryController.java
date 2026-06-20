package au.com.voc.raceEntry.entry;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService service;

    public EntryController(EntryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entry create(@RequestBody EntryRequest request) {
        return service.create(request.getBoatId(), request.getEventId());
    }

    @GetMapping("/event/{eventId}")
    public List<Entry> listByEvent(@PathVariable Long eventId) {
        return service.findByEventId(eventId);
    }

    @GetMapping("/{id}")
    public Entry getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/{id}/submit")
    public Entry submit(@PathVariable Long id) {
        return service.submit(id);
    }

    @PutMapping("/{id}/status")
    public Entry updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return service.updateStatus(id, request.getStatus());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Data
    static class EntryRequest {
        private Long boatId;
        private Long eventId;
    }

    @Data
    static class StatusRequest {
        private EntryStatus status;
    }
}
