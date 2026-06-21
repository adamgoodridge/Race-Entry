package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.mediator.EntryDriverMediator;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EntryDriverController {

    private final EntryDriverMediator mediator;

    public EntryDriverController(EntryDriverMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/api/entries/{entryId}/drivers")
    @ResponseStatus(HttpStatus.CREATED)
    public EntryDriver addDriver(@PathVariable Long entryId, @RequestBody DriverRequest request) {
        return mediator.addDriver(entryId, request);
    }

    @GetMapping("/api/entries/{entryId}/drivers")
    public List<EntryDriver> listDrivers(@PathVariable Long entryId) {
        return mediator.findByEntryId(entryId);
    }

    @PutMapping("/api/entry-drivers/{id}/role")
    public EntryDriver updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        return mediator.updateRole(id, request.getRole());
    }

    @DeleteMapping("/api/entry-drivers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDriver(@PathVariable Long id) {
        mediator.removeDriver(id);
    }

    @Data
    static class RoleRequest {
        private String role;
    }
}
