package au.com.voc.raceEntry.boat;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boats")
public class BoatController {

    private final BoatService service;

    public BoatController(BoatService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Boat create(@RequestBody BoatRequest request) {
        return service.create(request.getName(), request.getSailNumber(), request.getBoatClassId(), request.getOwnerId());
    }

    @GetMapping
    public List<Boat> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Boat getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Boat update(@PathVariable Long id, @RequestBody BoatRequest request) {
        return service.update(id, request.getName(), request.getSailNumber(), request.getBoatClassId());
    }

    @PutMapping("/{id}/owner/{personId}")
    public Boat assignOwner(@PathVariable Long id, @PathVariable Long personId) {
        return service.assignOwner(id, personId);
    }

    @DeleteMapping("/{id}/owner")
    public Boat removeOwner(@PathVariable Long id) {
        return service.removeOwner(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    static class BoatRequest {
        private String name;
        private String sailNumber;
        private Long boatClassId;
        private Long ownerId;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSailNumber() { return sailNumber; }
        public void setSailNumber(String sailNumber) { this.sailNumber = sailNumber; }

        public Long getBoatClassId() { return boatClassId; }
        public void setBoatClassId(Long boatClassId) { this.boatClassId = boatClassId; }

        public Long getOwnerId() { return ownerId; }
        public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    }
}
