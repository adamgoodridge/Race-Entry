package au.com.voc.raceEntry.entry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntryDriverRepository extends JpaRepository<EntryDriver, Long> {
    List<EntryDriver> findByEntryId(Long entryId);
    long countByEntryId(Long entryId);
    boolean existsByEntryIdAndPersonId(Long entryId, Long personId);
    Optional<EntryDriver> findByEntryIdAndPersonId(Long entryId, Long personId);
    void deleteByEntryId(Long entryId);
}
