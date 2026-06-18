package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.dto.BoatDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boats")
public class BoatController {

    private final BoatService boatService;

    public BoatController(BoatService boatService) {
        this.boatService = boatService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boat register(@RequestBody RegisterBoatRequest request) {
        return boatService.register(request.ownerId, request.name, request.boatClass);
    }

    @GetMapping
    public List<BoatDetailsDTO> listByOwner(@RequestParam Long ownerId) {
        return boatService.findByOwnerWithDetails(ownerId);
    }

    static class RegisterBoatRequest {
        public Long ownerId;
        public String name;
        public String boatClass;
    }
}
