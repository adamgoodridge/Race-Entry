//demo only
package au.com.voc.raceEntry.boat;


import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BoatService {

    private final BoatRepository boatRepository;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    @Autowired
    public BoatService(BoatRepository boatRepository, PersonRepository personRepository, UserRepository userRepository) {
        this.boatRepository = boatRepository;
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }

    public List<Boat> getAllBoats() {
        return boatRepository.findAll();
    }

    public List<Boat> getBoatsByUser(User user) {
        return boatRepository.boatsByUser(user);
    }


    public Boat getBoat(Long id) {
        return boatRepository.getReferenceById(id);
    }

    public List<Boat> getBoatsByOwner(Long ownerID) {
        return boatRepository.boatsByOwner(ownerID);
    }

    public List<Boat> byLicenseExpired(Long userID, LocalDate today) {
        return boatRepository.boatsByExpiredLicense(userID, today);
    }

    public void updateBoat(Boat boat, long driverId, long userId) {
        Person owner = personRepository.getReferenceById(driverId);
        User user = userRepository.getReferenceById(userId);
        boat.setUser(user);
        boat.setOwner(owner);
        boatRepository.save(boat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
    }
}
