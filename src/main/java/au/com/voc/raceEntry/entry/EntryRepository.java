//demo only
package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query("SELECT e FROM Entry e WHERE e.event.eventId = :eventId")
    List<Entry> entriesByEvent(@Param("eventId") Long eventId);

    @Query("SELECT e FROM Entry e WHERE e.user = :user and e.event.open = true ")
    List<Entry> entriesOpenByUser(@Param("user") User user);

    @Query("SELECT e FROM Entry e WHERE e.boat.boatId = :boatId and e.event.open = true")
    List<Entry> entriesByBoatOpened(@Param("boatId") Long boatId);

    @Query("SELECT e FROM Entry e WHERE e.event.open = true AND (e.driverOne.driverId = :driverId OR e.driverTwo.driverId = :driverId)")
    List<Entry> existsOpenEntriesByPerson(@Param("driverId") Long driverId);


}
