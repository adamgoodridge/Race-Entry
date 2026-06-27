package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventBoatClassRepository eventBoatClassRepository;
    private final BoatClassRepository boatClassRepository;

    public EventService(EventRepository eventRepository,
                        EventBoatClassRepository eventBoatClassRepository,
                        BoatClassRepository boatClassRepository) {
        this.eventRepository = eventRepository;
        this.eventBoatClassRepository = eventBoatClassRepository;
        this.boatClassRepository = boatClassRepository;
    }

    public Event create(EventRequest request) {
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setClosingDeadline(request.getClosingDeadline());
        event.setMaxDrivers(request.getMaxDrivers());
        return eventRepository.save(event);
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
    }

    public Event update(Long id, EventRequest request) {
        Event event = findById(id);
        if (request.getName() != null) event.setName(request.getName());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getStartDate() != null) event.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (request.getClosingDeadline() != null) event.setClosingDeadline(request.getClosingDeadline());
        if (request.getMaxDrivers() != null) event.setMaxDrivers(request.getMaxDrivers());
        return eventRepository.save(event);
    }

    public Event close(Long id) {
        Event event = findById(id);
        event.setStatus(EventStatus.CLOSED);
        return eventRepository.save(event);
    }

    public Event cancel(Long id) {
        Event event = findById(id);
        event.setStatus(EventStatus.CANCELLED);
        return eventRepository.save(event);
    }

    public EventBoatClass addBoatClass(Long eventId, Long boatClassId) {
        findById(eventId);
        boatClassRepository.findById(boatClassId)
                .orElseThrow(() -> new ResourceNotFoundException("BoatClass not found: " + boatClassId));
        if (eventBoatClassRepository.existsByEventIdAndBoatClassId(eventId, boatClassId)) {
            throw new ConflictException("BoatClass " + boatClassId + " already added to event " + eventId);
        }
        EventBoatClass ebc = new EventBoatClass();
        ebc.setEventId(eventId);
        ebc.setBoatClassId(boatClassId);
        return eventBoatClassRepository.save(ebc);
    }

    @Transactional
    public void removeBoatClass(Long eventId, Long boatClassId) {
        findById(eventId);
        EventBoatClass ebc = eventBoatClassRepository.findByEventIdAndBoatClassId(eventId, boatClassId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BoatClass " + boatClassId + " not found in event " + eventId));
        eventBoatClassRepository.delete(ebc);
    }

    public List<EventBoatClass> findBoatClasses(Long eventId) {
        findById(eventId);
        return eventBoatClassRepository.findByEventId(eventId);
    }

    public void closeExpiredEvents() {
        List<Event> expired = eventRepository.findByStatusAndClosingDeadlineBefore(
                EventStatus.OPEN, LocalDate.now());
        for (Event event : expired) {
            event.setStatus(EventStatus.CLOSED);
            eventRepository.save(event);
        }
    }
}
