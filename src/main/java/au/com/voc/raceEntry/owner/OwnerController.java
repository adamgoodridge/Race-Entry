package au.com.voc.raceEntry.owner;

import au.com.voc.raceEntry.mediator.RacingEventMediator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;
    private final RacingEventMediator mediator;

    public OwnerController(OwnerService ownerService, RacingEventMediator mediator) {
        this.ownerService = ownerService;
        this.mediator = mediator;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Owner create(@RequestBody CreateOwnerRequest request) {
        return ownerService.create(request.name, request.contactEmail);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        mediator.deleteOwner(id);
    }

    static class CreateOwnerRequest {
        public String name;
        public String contactEmail;
    }
}
