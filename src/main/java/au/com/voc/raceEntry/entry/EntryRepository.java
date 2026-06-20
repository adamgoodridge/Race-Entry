package au.com.voc.raceEntry.entry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {
    List<Entry> findByEventId(Long eventId);

    boolean existsByBoatIdAndEventId(Long boatId, Long eventId);
}
