package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.dto.BoatDetailsDTO;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryRepository;
import au.com.voc.raceEntry.entry.EntryStatus;
import au.com.voc.raceEntry.owner.Owner;
import au.com.voc.raceEntry.owner.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoatService {

    private final BoatRepository boatRepository;
    private final OwnerRepository ownerRepository;
    private final EntryRepository entryRepository;

    public BoatService(BoatRepository boatRepository, OwnerRepository ownerRepository,
                       EntryRepository entryRepository) {
        this.boatRepository = boatRepository;
        this.ownerRepository = ownerRepository;
        this.entryRepository = entryRepository;
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

    public List<BoatDetailsDTO> findByOwnerWithDetails(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + ownerId));
        return boatRepository.findByOwnerId(ownerId).stream()
                .map(boat -> {
                    Entry activeEntry = entryRepository.findByBoatId(boat.getBoatId()).stream()
                            .filter(e -> e.getStatus() == EntryStatus.ACTIVE)
                            .findFirst()
                            .orElse(null);
                    return new BoatDetailsDTO(boat, owner, activeEntry);
                })
                .collect(Collectors.toList());
    }

    public void markOrphaned(Long boatId) {
        Boat boat = findById(boatId);
        boat.setStatus(BoatStatus.ORPHANED);
        boatRepository.save(boat);
    }
}
