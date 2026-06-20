package au.com.voc.raceEntry.person;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person create(@RequestBody PersonRequest request) {
        return service.create(request.getFirstName(), request.getLastName(), request.getEmail());
    }

    @GetMapping
    public List<Person> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Person getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable Long id, @RequestBody PersonRequest request) {
        return service.update(id, request.getFirstName(), request.getLastName(), request.getEmail());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Data
    static class PersonRequest {
        private String firstName;
        private String lastName;
        private String email;
    }
}
