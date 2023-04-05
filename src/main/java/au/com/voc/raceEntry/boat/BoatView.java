//demo only
package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.utils.DateConstraint;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("unused")
@Entity
@Table(name = "boat")
public class BoatView {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "boat_id")
    private long boatId;
    @NotNull(message = "You must enter in a boat name.")
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @NotNull(message = "You must enter in a race number.")
    @Column(name = "race_no")
    private String raceNo;
    @Transient
    private Long userIId;
    @Basic
    @NotNull(message = "You must enter in a class nane.")
    @Column(name = "class_name")
    private String className;
    @Basic
    @NotNull(message = "You must enter the capacity of the motor.")
    @Column(name = "capacity_of_motor")
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
    @Column(name = "sba_registration_no")
    private String sbaRegistrationNo;
    @DateConstraint()
    @Column(name = "sba_registration_date")
//    @DateBeforeToday(message = "The licence expiry date must be later than today's date.")
    private LocalDate sbaRegistrationDate;
    @Column(name = "owner_id")
    @Min(value = 1, message = "You must select a owner")
    private Long ownerId;
    //when it is a new boat
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;
    private String previousUrl;
    private Boolean deleted;

    public BoatView() {
    }

    public BoatView(User user) {
        this.user = user;
        boatId = -1;
        deleted = false;
    }

    public BoatView(Boat boat) {
        boatId = boat.getBoatId();
        name = boat.getName();
        raceNo = boat.getRaceNo();
        className = boat.getClassName();
        capacityOfMotor = boat.getCapacityOfMotor();
        length = boat.getLength();
        beam = boat.getBeam();
        ownerId = boat.getOwner().getPersonId();
        userIId = boat.getUser().getId();
        sbaRegistrationNo = boat.getSbaRegistrationNo();
        sbaRegistrationDate = boat.getSbaLicenceExpiryDate();
        deleted = boat.getDeleted();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @SuppressWarnings("unused")
    public String getRaceNo() {
        return raceNo;
    }

    public void setRaceNo(String raceDno) {
        this.raceNo = raceDno;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setBeam(BigDecimal beam) {
        this.beam = beam;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public Long getUserId() {
        return userIId;
    }

    public void setUserId(Long userIId) {
        this.userIId = userIId;
    }

    public Boat toBoat(User user) {
        Boat boat = new Boat();
        if (boatId != -1)
            boat.setBoatId(boatId);
        boat.setName(name);
        boat.setRaceNo(raceNo);
        boat.setClassName(className);
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