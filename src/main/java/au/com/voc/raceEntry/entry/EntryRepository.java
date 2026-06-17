package au.com.voc.raceEntry.entry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    List<Entry> findByBoatId(Long boatId);

    List<Entry> findByEventId(Long eventId);

    @Query("SELECT e FROM Entry e WHERE e.eventId = :eventId AND e.status = :status")
    List<Entry> findByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") EntryStatus status);

    boolean existsByBoatIdAndEventId(Long boatId, Long eventId);

    boolean existsByBoatIdAndEventIdAndEntryIdNot(Long boatId, Long eventId, Long entryId);
}
