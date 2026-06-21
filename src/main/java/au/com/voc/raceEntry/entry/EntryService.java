package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryService {

    private final EntryRepository entryRepository;

    public EntryService(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public Entry create(Long boatId, Long eventId) {
        if (entryRepository.existsByBoatIdAndEventId(boatId, eventId)) {
            throw new ConflictException("Entry already exists for boat " + boatId + " in event " + eventId);
        }
        return entryRepository.save(new Entry(boatId, eventId));
    }

    public List<Entry> findByEventId(Long eventId) {
        return entryRepository.findByEventId(eventId);
    }

    public Entry findById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + id));
    }

    public Entry updateStatus(Long id, EntryStatus status) {
        Entry entry = findById(id);
        entry.setStatus(status);
        return entryRepository.save(entry);
    }

    public void delete(Long id) {
        if (!entryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entry not found: " + id);
        }
        entryRepository.deleteById(id);
    }
}
