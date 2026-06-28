package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryDriverService {

    private final EntryDriverRepository entryDriverRepository;

    public EntryDriverService(EntryDriverRepository entryDriverRepository) {
        this.entryDriverRepository = entryDriverRepository;
    }

    public EntryDriver addDriver(Long entryId, Long personId) {
        if (entryDriverRepository.existsByEntryIdAndPersonId(entryId, personId)) {
            throw new ConflictException(
                    "Person " + personId + " is already a driver on entry " + entryId);
        }
        EntryDriver driver = new EntryDriver();
        driver.setEntryId(entryId);
        driver.setPersonId(personId);
        return entryDriverRepository.save(driver);
    }

    public List<EntryDriver> findByEntryId(Long entryId) {
        return entryDriverRepository.findByEntryId(entryId);
    }

    public void removeDriver(Long entryId, Long personId) {
        EntryDriver driver = entryDriverRepository.findByEntryIdAndPersonId(entryId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Driver " + personId + " not found on entry " + entryId));
        entryDriverRepository.delete(driver);
    }
}
