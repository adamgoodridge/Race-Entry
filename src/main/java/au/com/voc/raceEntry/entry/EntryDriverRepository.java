package au.com.voc.raceEntry.entry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EntryDriverRepository extends JpaRepository<EntryDriverMap, Long> {

    @Query("SELECT m.driverId FROM EntryDriverMap m WHERE m.entryId = :entryId")
    List<Long> findDriverIdsByEntryId(@Param("entryId") Long entryId);

    @Query("SELECT m.entryId FROM EntryDriverMap m WHERE m.driverId = :driverId")
    List<Long> findEntryIdsByDriverId(@Param("driverId") Long driverId);
}
