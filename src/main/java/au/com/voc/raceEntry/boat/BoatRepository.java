package au.com.voc.raceEntry.boat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoatRepository extends JpaRepository<Boat, Long> {

    List<Boat> findByOwnerId(Long ownerId);
}
