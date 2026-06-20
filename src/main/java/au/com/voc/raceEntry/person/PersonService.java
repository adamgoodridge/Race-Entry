package au.com.voc.raceEntry.person;

import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public Person create(String firstName, String lastName, String email) {
        return repository.save(new Person(firstName, lastName, email));
    }

    public List<Person> findAll() {
        return repository.findAll();
    }

    public Person findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + id));
    }

    public Person update(Long id, String firstName, String lastName, String email) {
        Person person = findById(id);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setEmail(email);
        return repository.save(person);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Person not found: " + id);
        }
        repository.deleteById(id);
    }
}
