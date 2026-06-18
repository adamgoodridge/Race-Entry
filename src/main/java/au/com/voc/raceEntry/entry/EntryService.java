package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boat.BoatStatus;
import au.com.voc.raceEntry.driver.Driver;
import au.com.voc.raceEntry.driver.DriverRepository;
import au.com.voc.raceEntry.dto.EntryDetailsDTO;
import au.com.voc.raceEntry.event.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntryService {

    private final EntryRepository entryRepository;
    private final EntryDriverRepository entryDriverRepository;
    private final BoatRepository boatRepository;
    private final DriverRepository driverRepository;
    private final EventRepository eventRepository;

    public EntryService(EntryRepository entryRepository, EntryDriverRepository entryDriverRepository,
                        BoatRepository boatRepository, DriverRepository driverRepository,
                        EventRepository eventRepository) {
        this.entryRepository = entryRepository;
        this.entryDriverRepository = entryDriverRepository;
        this.boatRepository = boatRepository;
        this.driverRepository = driverRepository;
        this.eventRepository = eventRepository;
    }

    public Entry create(Long boatId, Long eventId) {
        Boat boat = boatRepository.findById(boatId)
                .orElseThrow(() -> new IllegalArgumentException("Boat not found: " + boatId));
        if (boat.getStatus() == BoatStatus.ORPHANED) {
            throw new IllegalStateException("Boat " + boatId + " is ORPHANED and cannot be entered in events");
        }
        eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
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

    public List<EntryDetailsDTO> findByEventWithDetails(Long eventId) {
        return entryRepository.findByEventId(eventId).stream()
                .map(this::toDetailsDTO)
                .collect(Collectors.toList());
    }

    public List<EntryDetailsDTO> findByBoatWithDetails(Long boatId) {
        return entryRepository.findByBoatId(boatId).stream()
                .map(this::toDetailsDTO)
                .collect(Collectors.toList());
    }

    public List<Driver> findDriversForEntry(Long entryId) {
        findById(entryId);
        return entryDriverRepository.findDriverIdsByEntryId(entryId).stream()
                .map(id -> driverRepository.findById(id)
                        .orElseThrow(() -> new IllegalStateException("Driver not found: " + id)))
                .collect(Collectors.toList());
    }

    private EntryDetailsDTO toDetailsDTO(Entry entry) {
        Boat boat = boatRepository.findById(entry.getBoatId())
                .orElseThrow(() -> new IllegalStateException("Boat not found: " + entry.getBoatId()));
        List<Driver> drivers = entryDriverRepository.findDriverIdsByEntryId(entry.getEntryId()).stream()
                .map(id -> driverRepository.findById(id)
                        .orElseThrow(() -> new IllegalStateException("Driver not found: " + id)))
                .collect(Collectors.toList());
        return new EntryDetailsDTO(entry, boat, drivers);
    }

    @Transactional
    public void updateStatus(Long entryId, EntryStatus newStatus) {
        Entry entry = findById(entryId);
        if (newStatus == EntryStatus.ACTIVE) {
            boolean hasDrivers = !entryDriverRepository.findDriverIdsByEntryId(entryId).isEmpty();
            if (!hasDrivers) {
                throw new IllegalStateException("Entry " + entryId + " must have at least one driver before becoming ACTIVE");
            }
        }
        if (newStatus == EntryStatus.CANCELLED) {
            entryDriverRepository.deleteByEntryId(entryId);
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
        driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + driverId));
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
