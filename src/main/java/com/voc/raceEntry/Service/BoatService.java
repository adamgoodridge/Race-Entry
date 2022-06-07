package com.voc.raceEntry.Service;

import com.voc.raceEntry.Entity.Boat;

import java.util.List;


public interface BoatService {
    public List<Boat> getBoats();
    public Boat getBoat(Long id);

    void updateBoat(Boat boat);
}
