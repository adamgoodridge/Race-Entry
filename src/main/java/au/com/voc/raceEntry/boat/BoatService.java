package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.owner.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatService {

    private final BoatRepository boatRepository;
    private final OwnerRepository ownerRepository;

    public BoatService(BoatRepository boatRepository, OwnerRepository ownerRepository) {
        this.boatRepository = boatRepository;
        this.ownerRepository = ownerRepository;
    }

    public Boat register(Long ownerId, String name, String boatClass) {
        ownerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + ownerId));
        Boat boat = new Boat();
        boat.setOwnerId(ownerId);
        boat.setName(name);
        boat.setBoatClass(boatClass);
        return boatRepository.save(boat);
    }

    public Boat findById(Long boatId) {
        return boatRepository.findById(boatId)
                .orElseThrow(() -> new IllegalArgumentException("Boat not found: " + boatId));
    }

    public List<Boat> findByOwner(Long ownerId) {
        return boatRepository.findByOwnerId(ownerId);
    }

    public void markOrphaned(Long boatId) {
        Boat boat = findById(boatId);
        boat.setStatus(BoatStatus.ORPHANED);
        boatRepository.save(boat);
    }
}
