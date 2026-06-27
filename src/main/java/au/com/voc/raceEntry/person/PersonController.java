package au.com.voc.raceEntry.person;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<Person> create(@RequestBody PersonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.create(request));
    }

    @GetMapping
    public List<Person> findAll() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public Person findById(@PathVariable Long id) {
        return personService.findById(id);
    }

    @PutMapping("/{id}")
    public Person update(@PathVariable Long id, @RequestBody PersonRequest request) {
        return personService.update(id, request);
    }

    @PutMapping("/{id}/user/{userId}")
    public Person reassignUser(@PathVariable Long id, @PathVariable Long userId) {
        return personService.reassignUser(id, userId);
    }
}
