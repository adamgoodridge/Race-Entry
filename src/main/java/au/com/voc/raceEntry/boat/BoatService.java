package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatService {

    private final BoatRepository boatRepository;

    public BoatService(BoatRepository boatRepository) {
        this.boatRepository = boatRepository;
    }

    public Boat create(String name, String raceNumber, Long ownerId) {
        if (boatRepository.existsByRaceNumber(raceNumber)) {
            throw new ConflictException("Race number already in use: " + raceNumber);
        }
        Boat boat = new Boat();
        boat.setName(name);
        boat.setRaceNumber(raceNumber);
        boat.setOwnerId(ownerId);
        return boatRepository.save(boat);
    }

    public List<Boat> findAll() {
        return boatRepository.findAll();
    }

    public List<Boat> findByOwnerId(Long ownerId) {
        return boatRepository.findByOwnerId(ownerId);
    }

    public Boat findById(Long id) {
        return boatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boat not found: " + id));
    }

    public Boat update(Long id, BoatRequest request) {
        Boat boat = findById(id);
        if (request.getName() != null) {
            boat.setName(request.getName());
        }
        if (request.getRaceNumber() != null && !request.getRaceNumber().equals(boat.getRaceNumber())) {
            if (boatRepository.existsByRaceNumberAndIdNot(request.getRaceNumber(), id)) {
                throw new ConflictException("Race number already in use: " + request.getRaceNumber());
            }
            boat.setRaceNumber(request.getRaceNumber());
        }
        return boatRepository.save(boat);
    }

    public Boat deactivate(Long id) {
        Boat boat = findById(id);
        boat.setStatus(BoatStatus.INACTIVE);
        return boatRepository.save(boat);
    }

    public Boat assignOwner(Long id, Long personId) {
        Boat boat = findById(id);
        boat.setOwnerId(personId);
        return boatRepository.save(boat);
    }

    public Boat removeOwner(Long id) {
        Boat boat = findById(id);
        boat.setOwnerId(null);
        return boatRepository.save(boat);
    }
}
