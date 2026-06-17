package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByEvent(Event event);

    boolean existsByEvent(Event event);

    boolean existsByBoatAndEvent(Boat boat, Event event);

    boolean existsByBoatAndEventAndIdNot(Boat boat, Event event, Long excludedEntryId);
}
