package au.com.voc.raceEntry.entry;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EntryDriverController {

    private final EntryDriverService service;

    public EntryDriverController(EntryDriverService service) {
        this.service = service;
    }

    @PostMapping("/api/entries/{entryId}/drivers")
    @ResponseStatus(HttpStatus.CREATED)
    public EntryDriver addDriver(@PathVariable Long entryId, @RequestBody DriverRequest request) {
        return service.addDriver(entryId, request.getPersonId(), request.getRole());
    }

    @GetMapping("/api/entries/{entryId}/drivers")
    public List<EntryDriver> listDrivers(@PathVariable Long entryId) {
        return service.findByEntryId(entryId);
    }

    @PutMapping("/api/entry-drivers/{id}/role")
    public EntryDriver updateRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        return service.updateRole(id, request.getRole());
    }

    @DeleteMapping("/api/entry-drivers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDriver(@PathVariable Long id) {
        service.removeDriver(id);
    }

    @Data
    static class DriverRequest {
        private Long personId;
        private String role;
    }

    @Data
    static class RoleRequest {
        private String role;
    }
}
