//demo only
package au.com.voc.raceEntry.boat;


import au.com.voc.raceEntry.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BoatRepository extends JpaRepository<Boat, Long> {
    @Query("SELECT b FROM Boat b WHERE b.user=:user and b.deleted = false")
    List<Boat> boatsByUser(@Param("user") User user);

    @Query("SELECT b FROM Boat b WHERE b.deleted = false AND b.owner.driverId = :ownerId ")
    List<Boat> boatsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT b FROM Boat b WHERE b.deleted = false AND b.user.id = :userId AND b.sbaLicenceExpiryDate < :date ")
    List<Boat> boatsByExpiredLicense(@Param("userId") Long userId, @Param("date") LocalDate date);


}
