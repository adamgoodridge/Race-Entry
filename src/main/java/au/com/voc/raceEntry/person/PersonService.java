package au.com.voc.raceEntry.person;

import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person create(PersonRequest request) {
        Person p = new Person();
        p.setFirstName(request.getFirstName());
        p.setLastName(request.getLastName());
        p.setEmail(request.getEmail());
        p.setPhone(request.getPhone());
        p.setStreetAddress(request.getStreetAddress());
        p.setSuburb(request.getSuburb());
        p.setState(request.getState());
        p.setPostcode(request.getPostcode());
        p.setSbaLicenceNumber(request.getSbaLicenceNumber());
        p.setSbaExpiryDate(request.getSbaExpiryDate());
        p.setUserId(request.getUserId());
        return personRepository.save(p);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found: " + id));
    }

    public Person update(Long id, PersonRequest request) {
        Person p = findById(id);
        if (request.getFirstName() != null) p.setFirstName(request.getFirstName());
        if (request.getLastName() != null) p.setLastName(request.getLastName());
        if (request.getEmail() != null) p.setEmail(request.getEmail());
        if (request.getPhone() != null) p.setPhone(request.getPhone());
        if (request.getStreetAddress() != null) p.setStreetAddress(request.getStreetAddress());
        if (request.getSuburb() != null) p.setSuburb(request.getSuburb());
        if (request.getState() != null) p.setState(request.getState());
        if (request.getPostcode() != null) p.setPostcode(request.getPostcode());
        if (request.getSbaLicenceNumber() != null) p.setSbaLicenceNumber(request.getSbaLicenceNumber());
        if (request.getSbaExpiryDate() != null) p.setSbaExpiryDate(request.getSbaExpiryDate());
        return personRepository.save(p);
    }

    public Person reassignUser(Long personId, Long userId) {
        Person p = findById(personId);
        p.setUserId(userId);
        return personRepository.save(p);
    }
}
