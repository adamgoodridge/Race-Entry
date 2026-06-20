package au.com.voc.raceEntry.boatclass;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boat-classes")
public class BoatClassController {

    private final BoatClassService service;

    public BoatClassController(BoatClassService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoatClass create(@RequestBody CreateBoatClassRequest request) {
        return service.create(request.getName());
    }

    @GetMapping
    public List<BoatClass> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BoatClass getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    static class CreateBoatClassRequest {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
