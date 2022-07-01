package com.voc.raceEntry.entry;

//https://www.wimdeblauwe.com/blog/2021/04/16/using-html-select-options-with-thymeleaf/

import javax.validation.constraints.Min;

public class EntryFormData {
    private long entryId;
    private long eventId;
    @Min(value = 1, message = "You must select a boat.")
    private long boatId;
    @Min(value = 1, message = "You must select a driver.")
    private long firstDriverId;
    private long secondDriverId;

    public long getBoatId() {
        return boatId;
    }

    public EntryFormData() {

    }
    //when data need to edited from the data
    public EntryFormData(Entry entry) {
        eventId = entry.getEvent().getEventId();
        entryId = entry.getEntryId();
        boatId = entry.getBoat().getBoatId();
        firstDriverId = entry.getDriver_one().getDriverId();
        secondDriverId = entry.getDriver_two().getDriverId();
    }

    public void setBoatId(long boatId) {
        this.boatId = boatId;
    }

    public long getFirstDriverId() {
        return firstDriverId;
    }

    public void setFirstDriverId(long firstDriverId) {
        this.firstDriverId = firstDriverId;
    }

    public long getSecondDriverId() {
        return secondDriverId;
    }

    public void setSecondDriverId(long secondDriverId) {
        this.secondDriverId = secondDriverId;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "EntryFormData{" +
                "entryId=" + entryId +
                ", eventId=" + eventId +
                ", boatId=" + boatId +
                ", firstDriverId=" + firstDriverId +
                ", secondDriverId=" + secondDriverId +
                '}';
    }
}
