package com.voc.raceEntry.Service;

import com.voc.raceEntry.Entity.Boat;
import com.voc.raceEntry.dao.BoatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatServiceImpl implements BoatService {

    @Autowired
    private BoatRepository boatRepository;

    @Override
    public List<Boat> getBoats() {
        return boatRepository.findAll();
    }

    @Override
    public Boat getBoat(Long id) {
        return boatRepository.getReferenceById(id);
    }

    @Override
    public void updateBoat(Boat boat) {
        boatRepository.save(boat);
    }
}
