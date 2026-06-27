package au.com.voc.raceEntry.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatusAndClosingDeadlineBefore(EventStatus status, LocalDate date);
}
