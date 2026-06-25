package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryDriverService {

    private final EntryDriverRepository driverRepository;

    public EntryDriverService(EntryDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public EntryDriver addDriver(Long entryId, DriverRequest request) {
        if (driverRepository.existsByEntryIdAndPersonId(entryId, request.getPersonId())) {
            throw new ConflictException("Person " + request.getPersonId() + " is already a driver on entry " + entryId);
        }
        EntryDriver driver = new EntryDriver(entryId, request.getPersonId());
        driver.setRole(request.getRole());
        return driverRepository.save(driver);
    }   

    public List<EntryDriver> findByEntryId(Long entryId) {
        return driverRepository.findByEntryId(entryId);
    }

    public long countByEntryId(Long entryId) {
        return driverRepository.countByEntryId(entryId);
    }

    public EntryDriver updateRole(Long entryDriverId, String role) {
        EntryDriver driver = driverRepository.findById(entryDriverId)
                .orElseThrow(() -> new ResourceNotFoundException("EntryDriver not found: " + entryDriverId));
        driver.setRole(role);
        return driverRepository.save(driver);
    }

    public void removeDriver(Long entryDriverId) {
        if (!driverRepository.existsById(entryDriverId)) {
            throw new ResourceNotFoundException("EntryDriver not found: " + entryDriverId);
        }
        driverRepository.deleteById(entryDriverId);
    }
}
