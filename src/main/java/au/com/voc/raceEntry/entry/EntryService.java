package au.com.voc.raceEntry.entry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final EntryDriverRepository entryDriverRepository;

    public EntryService(EntryRepository entryRepository, EntryDriverRepository entryDriverRepository) {
        this.entryRepository = entryRepository;
        this.entryDriverRepository = entryDriverRepository;
    }

    public Entry create(Long boatId, Long eventId) {
        boolean exists = entryRepository.findByBoatId(boatId).stream()
                .anyMatch(e -> e.getEventId().equals(eventId));
        if (exists) {
            throw new IllegalStateException("Boat " + boatId + " already has an entry for event " + eventId);
        }
        Entry entry = new Entry();
        entry.setBoatId(boatId);
        entry.setEventId(eventId);
        entry.setStatus(EntryStatus.PENDING);
        return entryRepository.save(entry);
    }

    public Entry findById(Long entryId) {
        return entryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found: " + entryId));
    }

    public List<Entry> findByEvent(Long eventId) {
        return entryRepository.findByEventId(eventId);
    }

    public List<Entry> findByBoat(Long boatId) {
        return entryRepository.findByBoatId(boatId);
    }

    public void updateStatus(Long entryId, EntryStatus newStatus) {
        Entry entry = findById(entryId);
        if (newStatus == EntryStatus.ACTIVE) {
            boolean hasDrivers = !entryDriverRepository.findDriverIdsByEntryId(entryId).isEmpty();
            if (!hasDrivers) {
                throw new IllegalStateException("Entry " + entryId + " must have at least one driver before becoming ACTIVE");
            }
        }
        entry.setStatus(newStatus);
        entryRepository.save(entry);
    }

    @Transactional
    public void cancel(Long entryId) {
        Entry entry = findById(entryId);
        entry.setStatus(EntryStatus.CANCELLED);
        entryRepository.save(entry);
        entryDriverRepository.deleteByEntryId(entryId);
    }

    public EntryDriverMap assignDriver(Long entryId, Long driverId, DriverRole role) {
        findById(entryId);
        boolean alreadyAssigned = entryDriverRepository.findDriverIdsByEntryId(entryId)
                .contains(driverId);
        if (alreadyAssigned) {
            throw new IllegalStateException("Driver " + driverId + " is already assigned to entry " + entryId);
        }
        EntryDriverMap map = new EntryDriverMap();
        map.setEntryId(entryId);
        map.setDriverId(driverId);
        map.setRole(role);
        return entryDriverRepository.save(map);
    }

    @Transactional
    public void removeDriver(Long entryId, Long driverId) {
        List<Long> driverIds = entryDriverRepository.findDriverIdsByEntryId(entryId);
        if (!driverIds.contains(driverId)) {
            throw new IllegalArgumentException("Driver " + driverId + " is not assigned to entry " + entryId);
        }
        if (driverIds.size() == 1) {
            throw new IllegalStateException("Cannot remove the last driver from entry " + entryId);
        }
        entryDriverRepository.deleteByEntryIdAndDriverId(entryId, driverId);
    }
}
