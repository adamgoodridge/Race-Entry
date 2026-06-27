package au.com.voc.raceEntry.boatclass;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoatClassRepository extends JpaRepository<BoatClass, Long> {
    List<BoatClass> findByStatus(BoatClassStatus status);
}
