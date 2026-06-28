package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService entryService;
    private final EntrySubmissionMediator submissionMediator;
    private final LateEntryApprovalMediator lateApprovalMediator;
    private final BoatService boatService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public EntryController(EntryService entryService,
                           EntrySubmissionMediator submissionMediator,
                           LateEntryApprovalMediator lateApprovalMediator,
                           BoatService boatService,
                           UserRepository userRepository,
                           PersonRepository personRepository) {
        this.entryService = entryService;
        this.submissionMediator = submissionMediator;
        this.lateApprovalMediator = lateApprovalMediator;
        this.boatService = boatService;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    @PostMapping
    public ResponseEntity<Entry> create(@RequestBody EntryRequest request,
                                        Authentication authentication) {
        if (!isAdmin(authentication)) {
            Boat boat = boatService.findById(request.getBoatId());
            Long callerPersonId = resolveCallerPersonId(authentication.getName());
            if (callerPersonId == null || !callerPersonId.equals(boat.getOwnerId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not the boat owner");
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(entryService.create(request.getBoatId(), request.getEventId(), request.getBoatClassId()));
    }

    @GetMapping
    public List<Entry> findAll(Authentication authentication) {
        if (isAdmin(authentication)) {
            return entryService.findAll();
        }
        Long personId = resolveCallerPersonId(authentication.getName());
        List<Long> boatIds = boatService.findByOwnerId(personId).stream()
                .map(Boat::getId)
                .collect(Collectors.toList());
        return entryService.findByBoatIds(boatIds);
    }

    @GetMapping("/{id}")
    public Entry findById(@PathVariable Long id) {
        return entryService.findById(id);
    }

    @PostMapping("/{id}/submit")
    public Entry submit(@PathVariable Long id, Authentication authentication) {
        requireOwnerOrAdmin(id, authentication);
        return submissionMediator.submit(id);
    }

    @PostMapping("/{id}/cancel")
    public Entry cancel(@PathVariable Long id, Authentication authentication) {
        requireOwnerOrAdmin(id, authentication);
        return entryService.cancel(id);
    }

    @PostMapping("/{id}/approve")
    public Entry approve(@PathVariable Long id) {
        return entryService.approve(id);
    }

    @PostMapping("/{id}/request-changes")
    public Entry requestChanges(@PathVariable Long id,
                                @RequestBody RequestChangesRequest request) {
        return entryService.requestChanges(id, request.getComment());
    }

    @PostMapping("/{id}/mark-paid")
    public Entry markPaid(@PathVariable Long id) {
        return entryService.markPaid(id);
    }

    @PostMapping("/{id}/late-approve")
    public Entry lateApprove(@PathVariable Long id) {
        return lateApprovalMediator.lateApprove(id);
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
