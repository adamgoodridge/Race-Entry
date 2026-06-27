package au.com.voc.raceEntry.boatclass;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boat-classes")
public class BoatClassController {

    private final BoatClassService boatClassService;

    public BoatClassController(BoatClassService boatClassService) {
        this.boatClassService = boatClassService;
    }

    @PostMapping
    public ResponseEntity<BoatClass> create(@RequestBody BoatClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boatClassService.create(request));
    }

    @GetMapping
    public List<BoatClass> findAll(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return isAdmin ? boatClassService.findAll() : boatClassService.findAllActive();
    }

    @GetMapping("/{id}")
    public BoatClass findById(@PathVariable Long id) {
        return boatClassService.findById(id);
    }

    @PatchMapping("/{id}/deactivate")
    public BoatClass deactivate(@PathVariable Long id) {
        return boatClassService.deactivate(id);
    }
}
