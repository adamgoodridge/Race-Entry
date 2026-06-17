package au.com.voc.raceEntry.driver;

import au.com.voc.raceEntry.entry.DriverRole;
import au.com.voc.raceEntry.entry.EntryDriverMap;
import au.com.voc.raceEntry.entry.EntryDriverRepository;
import au.com.voc.raceEntry.entry.EntryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DriverController {

    private final DriverService driverService;
    private final EntryService entryService;
    private final EntryDriverRepository entryDriverRepository;
    private final DriverRepository driverRepository;

    public DriverController(DriverService driverService, EntryService entryService,
                            EntryDriverRepository entryDriverRepository, DriverRepository driverRepository) {
        this.driverService = driverService;
        this.entryService = entryService;
        this.entryDriverRepository = entryDriverRepository;
        this.driverRepository = driverRepository;
    }

    @PostMapping("/drivers")
    @ResponseStatus(HttpStatus.CREATED)
    public Driver create(@RequestBody CreateDriverRequest request) {
        return driverService.create(request.name, request.licenseNumber);
    }

    @PostMapping("/entries/{id}/drivers")
    @ResponseStatus(HttpStatus.CREATED)
    public EntryDriverMap assignDriver(@PathVariable Long id, @RequestBody AssignDriverRequest request) {
        return entryService.assignDriver(id, request.driverId, request.role);
    }

    @DeleteMapping("/entries/{id}/drivers/{driverId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDriver(@PathVariable Long id, @PathVariable Long driverId) {
        entryService.removeDriver(id, driverId);
    }

    @GetMapping("/entries/{id}/drivers")
    public List<Driver> listDrivers(@PathVariable Long id) {
        List<Long> driverIds = entryDriverRepository.findDriverIdsByEntryId(id);
        return driverIds.stream()
                .map(driverId -> driverRepository.findById(driverId)
                        .orElseThrow(() -> new IllegalStateException("Driver not found: " + driverId)))
                .collect(Collectors.toList());
    }

    static class CreateDriverRequest {
        public String name;
        public String licenseNumber;
    }

    static class AssignDriverRequest {
        public Long driverId;
        public DriverRole role;
    }
}
