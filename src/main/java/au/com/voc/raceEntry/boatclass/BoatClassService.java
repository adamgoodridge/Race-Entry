package au.com.voc.raceEntry.boatclass;

import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatClassService {

    private final BoatClassRepository repository;

    public BoatClassService(BoatClassRepository repository) {
        this.repository = repository;
    }

    public BoatClass create(String name) {
        return repository.save(new BoatClass(name));
    }

    public List<BoatClass> findAll() {
        return repository.findAll();
    }

    public BoatClass findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BoatClass not found: " + id));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("BoatClass not found: " + id);
        }
        repository.deleteById(id);
    }
}
