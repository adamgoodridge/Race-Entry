package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class EntryDriverController {

    private final EntryDriverService entryDriverService;
    private final EntryService entryService;
    private final BoatService boatService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public EntryDriverController(EntryDriverService entryDriverService,
                                  EntryService entryService,
                                  BoatService boatService,
                                  UserRepository userRepository,
                                  PersonRepository personRepository) {
        this.entryDriverService = entryDriverService;
        this.entryService = entryService;
        this.boatService = boatService;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    @PostMapping("/{entryId}/drivers")
    public ResponseEntity<EntryDriver> addDriver(@PathVariable Long entryId,
                                                  @RequestBody AddDriverRequest request,
                                                  Authentication authentication) {
        requireOwnerOrAdmin(entryId, authentication);
        personRepository.findById(request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found: " + request.getPersonId()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entryDriverService.addDriver(entryId, request.getPersonId()));
    }

    @GetMapping("/{entryId}/drivers")
    public List<EntryDriver> listDrivers(@PathVariable Long entryId) {
        entryService.findById(entryId);
        return entryDriverService.findByEntryId(entryId);
    }

    @DeleteMapping("/{entryId}/drivers/{personId}")
    public ResponseEntity<Void> removeDriver(@PathVariable Long entryId,
                                              @PathVariable Long personId,
                                              Authentication authentication) {
        requireOwnerOrAdmin(entryId, authentication);
        entryDriverService.removeDriver(entryId, personId);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private Long resolveCallerPersonId(String email) {
        return userRepository.findByEmail(email)
                .flatMap(u -> personRepository.findByUserId(u.getId()))
                .map(p -> p.getId())
                .orElse(null);
    }

    private void requireOwnerOrAdmin(Long entryId, Authentication authentication) {
        if (isAdmin(authentication)) return;
        Entry entry = entryService.findById(entryId);
        Boat boat = boatService.findById(entry.getBoatId());
        Long callerPersonId = resolveCallerPersonId(authentication.getName());
        if (callerPersonId == null || !callerPersonId.equals(boat.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the boat owner");
        }
    }
}
