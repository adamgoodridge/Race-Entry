//demo only
package au.com.voc.raceEntry.person;

import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserRepository;
import au.com.voc.raceEntry.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;


@Service
public class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);


    @Autowired
    private PersonRepository personRepository;
    @Autowired
    @Deprecated
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Deprecated
    public List<Person> getPersons() {
        User user = userService.getCurrentUser();
        List<Person> drivers;
        if (user.isAdmin()) {
            drivers = personRepository.findAll();
        } else {

            //drivers = personRepository.driversByUserName(Long.parseLong("4"));
            log.debug("{}", userService.getCurrentUser().getUsername());
            log.debug("================================");
            log.debug("{}", user.getUsername());
            drivers = personRepository.driversByUserName(user);
        }
        return drivers;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public List<Person> getPersonsByUser(User user) {
        return personRepository.driversByUserName(user);
    }

    public List<Person> getPersonsByLicenseExpired(Long userId) {
        return personRepository.personsByExpiredLicense(userId, LocalDate.now());
    }

    public void deletePerson(long id) {

        Person driver = personRepository.getReferenceById(id);
        personRepository.delete(driver);
    }

    public Person getPerson(long id) {
        return personRepository.getReferenceById(id);
    }


    //add or update
    public void updatePerson(Person person) {
        User user = userRepository.getReferenceById(person.getUserId());
        person.setUser(user);
        personRepository.save(person);
    }

}
