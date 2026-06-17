package au.com.voc.raceEntry.entry;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entries")
public class EntryController {

    private final EntryService entryService;

    public EntryController(EntryService entryService) {
        this.entryService = entryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Entry create(@RequestBody CreateEntryRequest request) {
        return entryService.create(request.boatId, request.eventId);
    }

    @GetMapping
    public List<Entry> list(@RequestParam(required = false) Long eventId,
                            @RequestParam(required = false) Long boatId) {
        if (eventId != null) {
            return entryService.findByEvent(eventId);
        }
        if (boatId != null) {
            return entryService.findByBoat(boatId);
        }
        throw new IllegalArgumentException("Either eventId or boatId must be provided");
    }

    @PatchMapping("/{id}/status")
    public Entry updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        entryService.updateStatus(id, request.status);
        return entryService.findById(id);
    }

    static class CreateEntryRequest {
        public Long boatId;
        public Long eventId;
    }

    static class UpdateStatusRequest {
        public EntryStatus status;
    }
}
