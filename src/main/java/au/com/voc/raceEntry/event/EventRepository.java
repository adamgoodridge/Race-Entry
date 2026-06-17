package au.com.voc.raceEntry.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatusAndVisible(EventStatus status, Boolean visible);
}
