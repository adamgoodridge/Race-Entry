package au.com.voc.raceEntry.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventViewRepository extends JpaRepository<EventView, Long> {
    @Query("SELECT e FROM EventView e WHERE e.open = :open")
    List<EventView> events(@Param("open") int open);
}
