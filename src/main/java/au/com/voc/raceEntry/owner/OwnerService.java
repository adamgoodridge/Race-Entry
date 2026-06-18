package au.com.voc.raceEntry.owner;

import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    public Owner create(String name, String contactEmail) {
        if (ownerRepository.findByContactEmail(contactEmail).isPresent()) {
            throw new IllegalStateException("An owner with email '" + contactEmail + "' already exists");
        }
        Owner owner = new Owner();
        owner.setName(name);
        owner.setContactEmail(contactEmail);
        return ownerRepository.save(owner);
    }

    public Owner findById(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + ownerId));
    }

    public void delete(Long ownerId) {
        findById(ownerId);
        ownerRepository.deleteById(ownerId);
    }
}
