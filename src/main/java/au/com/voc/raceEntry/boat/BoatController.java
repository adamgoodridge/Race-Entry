package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/boats")
public class BoatController {

    private final BoatService boatService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public BoatController(BoatService boatService, UserRepository userRepository, PersonRepository personRepository) {
        this.boatService = boatService;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    @PostMapping
    public ResponseEntity<Boat> create(@RequestBody BoatRequest request, Authentication authentication) {
        Long ownerId = resolveCallerPersonId(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                boatService.create(request.getName(), request.getRaceNumber(), ownerId));
    }

    @GetMapping
    public List<Boat> findAll(Authentication authentication) {
        if (isAdmin(authentication)) {
            return boatService.findAll();
        }
        Long personId = resolveCallerPersonId(authentication.getName());
        return boatService.findByOwnerId(personId);
    }

    @GetMapping("/{id}")
    public Boat findById(@PathVariable Long id) {
        return boatService.findById(id);
    }

    @PutMapping("/{id}")
    public Boat update(@PathVariable Long id, @RequestBody BoatRequest request, Authentication authentication) {
        requireOwnerOrAdmin(id, authentication);
        return boatService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public Boat deactivate(@PathVariable Long id, Authentication authentication) {
        requireOwnerOrAdmin(id, authentication);
        return boatService.deactivate(id);
    }

    @PutMapping("/{id}/owner/{personId}")
    public Boat assignOwner(@PathVariable Long id, @PathVariable Long personId) {
        return boatService.assignOwner(id, personId);
    }

    @DeleteMapping("/{id}/owner")
    public Boat removeOwner(@PathVariable Long id) {
        return boatService.removeOwner(id);
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

    private void requireOwnerOrAdmin(Long boatId, Authentication authentication) {
        if (isAdmin(authentication)) return;
        Boat boat = boatService.findById(boatId);
        Long callerPersonId = resolveCallerPersonId(authentication.getName());
        if (callerPersonId == null || !callerPersonId.equals(boat.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the boat owner");
        }
    }
}
