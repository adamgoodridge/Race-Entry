package com.voc.raceEntry.entry;

import com.sun.istack.NotNull;
import com.voc.raceEntry.boat.Boat;
import com.voc.raceEntry.driver.Driver;
import com.voc.raceEntry.event.Event;

import javax.persistence.*;

@Entity
public class Entry {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "entry_id")
    private long entryId;
    @ManyToOne(fetch = FetchType.LAZY, cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "boat_id")
    @NotNull
    private Boat boat;
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @NotNull
    @JoinColumn(name = "driver_1_id")
    private Driver driver_one;
    @ManyToOne(cascade={CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "driver_2_id")
    private Driver driver_two;

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Boat getBoat() {
        return boat;
    }

    public void setBoat(Boat boat) {
        this.boat = boat;
    }

    public Driver getDriver_one() {
        return driver_one;
    }

    public void setDriver_one(Driver firstDriverId) {
        this.driver_one = firstDriverId;
    }

    public Driver getDriver_two() {
        return driver_two;
    }

    public void setDriver_two(Driver secondDriverId) {
        this.driver_two = secondDriverId;
    }

}


