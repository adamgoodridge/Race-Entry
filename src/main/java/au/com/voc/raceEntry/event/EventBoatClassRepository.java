package au.com.voc.raceEntry.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventBoatClassRepository extends JpaRepository<EventBoatClass, Long> {
    List<EventBoatClass> findByEventId(Long eventId);
    boolean existsByEventIdAndBoatClassId(Long eventId, Long boatClassId);
    Optional<EventBoatClass> findByEventIdAndBoatClassId(Long eventId, Long boatClassId);
}
