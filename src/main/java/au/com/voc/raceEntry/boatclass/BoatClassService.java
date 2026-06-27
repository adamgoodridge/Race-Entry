package au.com.voc.raceEntry.boatclass;

import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatClassService {

    private final BoatClassRepository boatClassRepository;

    public BoatClassService(BoatClassRepository boatClassRepository) {
        this.boatClassRepository = boatClassRepository;
    }

    public BoatClass create(BoatClassRequest request) {
        BoatClass bc = new BoatClass();
        bc.setName(request.getName());
        return boatClassRepository.save(bc);
    }

    public List<BoatClass> findAll() {
        return boatClassRepository.findAll();
    }

    public List<BoatClass> findAllActive() {
        return boatClassRepository.findByStatus(BoatClassStatus.ACTIVE);
    }

    public BoatClass findById(Long id) {
        return boatClassRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BoatClass not found: " + id));
    }

    public BoatClass deactivate(Long id) {
        BoatClass bc = findById(id);
        bc.setStatus(BoatClassStatus.INACTIVE);
        return boatClassRepository.save(bc);
    }
}
