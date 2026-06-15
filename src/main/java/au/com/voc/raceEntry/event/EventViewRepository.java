//demo only
package au.com.voc.raceEntry.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventViewRepository extends JpaRepository<Event, Long> {
    @Query("SELECT new au.com.voc.raceEntry.event.EventFormData(e.eventId, e.name, e.venue, e.startDate, e.duration, e.open, e.visible, (SELECT COUNT(en) FROM Entry en WHERE en.event = e)) FROM Event e WHERE e.open = :open")
    List<EventFormData> events(@Param("open") Boolean open);
}
