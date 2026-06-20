package au.com.voc.raceEntry.entry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntryDriverRepository extends JpaRepository<EntryDriver, Long> {
    long countByEntryId(Long entryId);
    List<EntryDriver> findByEntryId(Long entryId);
    boolean existsByEntryIdAndPersonId(Long entryId, Long personId);
}
