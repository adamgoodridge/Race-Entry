package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.utils.DateConstraint;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BoatFormData {
    private long boatId;
    @NotNull(message = "You must enter in a boat name.")
    private String name;
    @NotNull(message = "You must enter in a race number.")
    private String raceNo;
    private Long userId;
    @NotNull(message = "You must enter in a class nane.")
    private String boatType;
    @NotNull(message = "You must enter the capacity of the motor.")
    private String capacityOfMotor;
    @NotNull(message = "Value must between 0 & 25")
    @Min(value = 1, message = "Value must between 0 & 25")
    @Max(value = 26, message = "Value must between 0 & 25")
    private BigDecimal length;
    @NotNull(message = "Value must between 0 & 25")
    @Min(value = 1, message = "Value must between 0 & 25")
    @Max(value = 26, message = "Value must between 0 & 25")
    private BigDecimal beam;
    @NotNull(message = "You must enter a SBA registration number.")
    private String sbaRegistrationNo;
    @DateConstraint
    private LocalDate sbaRegistrationDate;
    @Min(value = 1, message = "You must select a owner")
    private Long ownerId;
    private Boolean deleted;

    public BoatFormData() {
    }

    public BoatFormData(User user) {
        this.userId = user.getId();
        boatId = -1;
        deleted = false;
    }

    public BoatFormData(Boat boat) {
        boatId = boat.getBoatId();
        name = boat.getName();
        raceNo = boat.getRaceNo();
        boatType = boat.getBoatType();
        capacityOfMotor = boat.getCapacityOfMotor();
        length = boat.getLength();
        beam = boat.getBeam();
        ownerId = boat.getOwner().getPersonId();
        userId = boat.getUser().getId();
        sbaRegistrationNo = boat.getSbaRegistrationNo();
        sbaRegistrationDate = boat.getSbaLicenceExpiryDate();
        deleted = boat.getDeleted();
    }

    public long getBoatId() {
        return boatId;
    }

    public void setBoatId(long boatId) {
        this.boatId = boatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaceNo() {
        return raceNo;
    }

    public void setRaceNo(String raceNo) {
        this.raceNo = raceNo;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getBoatType() {
        return boatType;
    }

    public void setBoatType(String boatType) {
        this.boatType = boatType;
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

    public String getSbaRegistrationNo() {
        return sbaRegistrationNo;
    }

    public void setSbaRegistrationNo(String sbaRegistrationNo) {
        this.sbaRegistrationNo = sbaRegistrationNo;
    }

    public LocalDate getSbaRegistrationDate() {
        return sbaRegistrationDate;
    }

    public void setSbaRegistrationDate(LocalDate sbaRegistrationDate) {
        this.sbaRegistrationDate = sbaRegistrationDate;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boat toBoat(User user) {
        Boat boat = new Boat();
        if (boatId != -1)
            boat.setBoatId(boatId);
        boat.setName(name);
        boat.setRaceNo(raceNo);
        boat.setBoatType(boatType);
        boat.setCapacityOfMotor(capacityOfMotor);
        boat.setLength(length);
        boat.setBeam(beam);
        boat.setUser(user);
        boat.setSbaRegistrationNo(sbaRegistrationNo);
        boat.setSbaLicenceDate(sbaRegistrationDate);
        boat.setDeleted(deleted);
        return boat;
    }
}
