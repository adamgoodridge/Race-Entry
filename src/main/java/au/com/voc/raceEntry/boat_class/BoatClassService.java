//demo only
package au.com.voc.raceEntry.boat_class;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatClassService {
    private final BoatClassRepository boatClassRepository;

    @Autowired
    public BoatClassService(BoatClassRepository boatClassRepository) {
        this.boatClassRepository = boatClassRepository;
    }

    public List<BoatClass> getBoatClasses() {
        return boatClassRepository.findAll();
    }
}
