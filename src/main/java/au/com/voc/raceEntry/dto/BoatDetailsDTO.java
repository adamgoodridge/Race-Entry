package au.com.voc.raceEntry.dto;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.owner.Owner;

public class BoatDetailsDTO {

    private final Boat boat;
    private final Owner owner;
    private final Entry activeEntry;

    public BoatDetailsDTO(Boat boat, Owner owner, Entry activeEntry) {
        this.boat = boat;
        this.owner = owner;
        this.activeEntry = activeEntry;
    }

    public Boat getBoat() { return boat; }
    public Owner getOwner() { return owner; }
    public Entry getActiveEntry() { return activeEntry; }
}
