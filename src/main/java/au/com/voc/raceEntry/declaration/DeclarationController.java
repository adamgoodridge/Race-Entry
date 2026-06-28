package au.com.voc.raceEntry.declaration;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.user.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/entries")
public class DeclarationController {

    private final DeclarationFormService declarationFormService;
    private final EntryService entryService;
    private final BoatService boatService;
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public DeclarationController(DeclarationFormService declarationFormService,
                                 EntryService entryService,
                                 BoatService boatService,
                                 UserRepository userRepository,
                                 PersonRepository personRepository) {
        this.declarationFormService = declarationFormService;
        this.entryService = entryService;
        this.boatService = boatService;
        this.userRepository = userRepository;
        this.personRepository = personRepository;
    }

    @GetMapping("/{id}/declaration")
    public ResponseEntity<byte[]> getDeclaration(@PathVariable Long id,
                                                  Authentication authentication) {
        requireOwnerOrAdmin(id, authentication);
        byte[] pdf = declarationFormService.generate(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "declaration-" + id + ".pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
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
