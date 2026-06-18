package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.dto.EntryDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    public Entry create(@Valid @RequestBody CreateEntryRequest request) {
        return entryService.create(request.boatId, request.eventId);
    }

    @GetMapping
    public List<EntryDetailsDTO> list(@RequestParam(required = false) Long eventId,
                                      @RequestParam(required = false) Long boatId) {
        if (eventId != null) {
            return entryService.findByEventWithDetails(eventId);
        }
        if (boatId != null) {
            return entryService.findByBoatWithDetails(boatId);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Either eventId or boatId must be provided");
    }

    @PatchMapping("/{id}/status")
    public Entry updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        entryService.updateStatus(id, request.status);
        return entryService.findById(id);
    }

    static class CreateEntryRequest {
        @NotNull(message = "boatId is required")
        public Long boatId;
        @NotNull(message = "eventId is required")
        public Long eventId;
    }

    static class UpdateStatusRequest {
        @NotNull(message = "status is required")
        public EntryStatus status;
    }
}
