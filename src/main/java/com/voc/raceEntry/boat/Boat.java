package com.voc.raceEntry.boat;

import com.voc.raceEntry.driver.Driver;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class Boat {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "boat_id")
    private long boatId;
    @NotNull(message = "You must enter in a boat name.")
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "race_no")
    private String raceNo;
    @Basic
    @Column(name = "class_name")
    private String className;
    @Basic
    @Column(name = "capacity_of_motor")
    private String capacityOfMotor;
    @Basic
    @Column(name = "length")
    private BigDecimal length;
    @Basic
    @Column(name = "beam")
    private BigDecimal beam;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "owner_id")
    private Driver owner;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaceNo() {
        return raceNo;
    }

    public void setRaceNo(String raceDno) {
        this.raceNo = raceDno;
    }


    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCapacityOfMotor() {
        return capacityOfMotor;
    }

    public void setCapacityOfMotor(String capacityOfMotor) {
        this.capacityOfMotor = capacityOfMotor;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getBeam() {
        return beam;
    }

    public void setBeam(BigDecimal beam) {
        this.beam = beam;
    }

    public long getBoatId() {
        return boatId;
    }

    public void setBoatId(long boatId) {
        this.boatId = boatId;
    }

    public Driver getOwner() {
        return owner;
    }

    public void setOwner(Driver owner) {
        this.owner = owner;
    }
}