package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.mediator.RaceEntryMediator;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService entryService;
    private final RaceEntryMediator mediator;

    public EntryController(EntryService entryService, RaceEntryMediator mediator) {
        this.entryService = entryService;
        this.mediator = mediator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entry create(@RequestBody EntryRequest request) {
        return entryService.create(request.getBoatId(), request.getEventId());
    }

    @GetMapping("/event/{eventId}")
    public List<Entry> listByEvent(@PathVariable Long eventId) {
        return entryService.findByEventId(eventId);
    }

    @GetMapping("/{id}")
    public Entry getById(@PathVariable Long id) {
        return entryService.findById(id);
    }

    @PostMapping("/{id}/submit")
    public Entry submit(@PathVariable Long id) {
        return mediator.submit(id);
    }

    @PutMapping("/{id}/status")
    public Entry updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return entryService.updateStatus(id, request.getStatus());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        entryService.delete(id);
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
