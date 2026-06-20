package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatService {

    private final BoatRepository repository;

    public BoatService(BoatRepository repository) {
        this.repository = repository;
    }

    public Boat create(String name, String sailNumber, Long boatClassId, Long ownerId) {
        return repository.save(new Boat(name, sailNumber, boatClassId, ownerId));
    }

    public List<Boat> findAll() {
        return repository.findAll();
    }

    public Boat findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Boat not found: " + id));
    }

    public Boat update(Long id, String name, String sailNumber, Long boatClassId) {
        Boat boat = findById(id);
        boat.setName(name);
        boat.setSailNumber(sailNumber);
        boat.setBoatClassId(boatClassId);
        return repository.save(boat);
    }

    public Boat assignOwner(Long id, Long ownerId) {
        Boat boat = findById(id);
        boat.setOwnerId(ownerId);
        return repository.save(boat);
    }

    public Boat removeOwner(Long id) {
        Boat boat = findById(id);
        boat.setOwnerId(null);
        return repository.save(boat);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Boat not found: " + id);
        }
        repository.deleteById(id);
    }
}
