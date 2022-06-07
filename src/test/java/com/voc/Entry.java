package com.voc;

import javax.persistence.*;

@Entity
public class Entry {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "entry_id")
    private int entryId;
    @Basic
    @Column(name = "event_id")
    private Integer eventId;
    @Basic
    @Column(name = "boat_id")
    private Integer boatId;
    @Basic
    @Column(name = "first_driver_id")
    private Integer firstDriverId;
    @Basic
    @Column(name = "second_driver_id")
    private Integer secondDriverId;
    @Basic
    @Column(name = "owner_id")
    private Integer ownerId;

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getBoatId() {
        return boatId;
    }

    public void setBoatId(Integer boatId) {
        this.boatId = boatId;
    }

    public Integer getFirstDriverId() {
        return firstDriverId;
    }

    public void setFirstDriverId(Integer firstDriverId) {
        this.firstDriverId = firstDriverId;
    }

    public Integer getSecondDriverId() {
        return secondDriverId;
    }

    public void setSecondDriverId(Integer secondDriverId) {
        this.secondDriverId = secondDriverId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry entry = (Entry) o;

        if (entryId != entry.entryId) return false;
        if (eventId != null ? !eventId.equals(entry.eventId) : entry.eventId != null) return false;
        if (boatId != null ? !boatId.equals(entry.boatId) : entry.boatId != null) return false;
        if (firstDriverId != null ? !firstDriverId.equals(entry.firstDriverId) : entry.firstDriverId != null)
            return false;
        if (secondDriverId != null ? !secondDriverId.equals(entry.secondDriverId) : entry.secondDriverId != null)
            return false;
        if (ownerId != null ? !ownerId.equals(entry.ownerId) : entry.ownerId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entryId;
        result = 31 * result + (eventId != null ? eventId.hashCode() : 0);
        result = 31 * result + (boatId != null ? boatId.hashCode() : 0);
        result = 31 * result + (firstDriverId != null ? firstDriverId.hashCode() : 0);
        result = 31 * result + (secondDriverId != null ? secondDriverId.hashCode() : 0);
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        return result;
    }
}
