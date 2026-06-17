package au.com.voc.raceEntry.dto;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.driver.Driver;
import au.com.voc.raceEntry.entry.Entry;

import java.util.List;

public class EntryDetailsDTO {

    private final Entry entry;
    private final Boat boat;
    private final List<Driver> drivers;

    public EntryDetailsDTO(Entry entry, Boat boat, List<Driver> drivers) {
        this.entry = entry;
        this.boat = boat;
        this.drivers = drivers;
    }

    public Entry getEntry() { return entry; }
    public Boat getBoat() { return boat; }
    public List<Driver> getDrivers() { return drivers; }
}
