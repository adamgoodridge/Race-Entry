package com.voc.raceEntry.boat;

import com.voc.raceEntry.driver.Driver;
import com.voc.raceEntry.driver.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoatService {

    @Autowired
    private BoatRepository boatRepository;

    @Autowired
    private DriverRepository driverRepository;


    public List<Boat> getBoats() {
        return boatRepository.findAll();
    }


    public Boat getBoat(Long id) {
        return boatRepository.getReferenceById(id);
    }


    public void updateBoat(BoatView boatView) {
        Driver owner = driverRepository.getReferenceById(boatView.getOwnerId());
        Boat boat = boatView.toBoat();
        boat.setOwner(owner);
        boatRepository.save(boat);
    }
}
