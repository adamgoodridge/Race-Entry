//demo only
package au.com.voc.raceEntry.person;

import au.com.voc.raceEntry.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT d FROM Person d WHERE d.user=:user")
    List<Person> driversByUserName(@Param("user") User user);

    @Query("SELECT p FROM Person p WHERE p.deleted = false AND p.user.id = :userId AND p.sbaLicenceExpiryDate  < :date ")
    List<Person> personsByExpiredLicense(@Param("userId") Long userId, @Param("date") LocalDate date);
}

