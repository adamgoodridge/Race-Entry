package au.com.voc.raceEntry.entry;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryDriverRepository extends JpaRepository<EntryDriver, Long> {
    long countByEntryId(Long entryId);
}
