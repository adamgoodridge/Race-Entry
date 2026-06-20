package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public Event create(String name) {
        return repository.save(new Event(name));
    }

    public List<Event> findAll() {
        return repository.findAll();
    }

    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    public Event update(Long id, String name) {
        Event event = findById(id);
        event.setName(name);
        return repository.save(event);
    }

    public Event close(Long id) {
        Event event = findById(id);
        event.setStatus(EventStatus.CLOSED);
        return repository.save(event);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }
        repository.deleteById(id);
    }
}
