//demo only
package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.utils.LicenceExpiry;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Boat implements LicenceExpiry {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "boat_id")
    private long boatId;
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
    @Column(name = "sba_registration_no")
    private String sbaRegistrationNo;
    @Column(name = "sba_registration_date")
    private LocalDate sbaLicenceExpiryDate;
    @Basic
    @Column(name = "length")
    private BigDecimal length;
    @Basic
    @Column(name = "beam")
    private BigDecimal beam;

    @Transient
    private Long userId;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "owner_id")
    private Person owner;
    @Column(name = "deleted", nullable = false, columnDefinition = "BIT", length = 1)
    private Boolean deleted;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaceNo() {
        return raceNo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setBeam(BigDecimal beam) {
        this.beam = beam;
    }

    public long getBoatId() {
        return boatId;
    }

    public void setBoatId(long boatId) {
        this.boatId = boatId;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }


    public String getSbaRegistrationNo() {
        return sbaRegistrationNo;
    }

    public void setSbaRegistrationNo(String sbaRegistrationNo) {
        this.sbaRegistrationNo = sbaRegistrationNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getSbaLicenceExpiryDate() {
        return sbaLicenceExpiryDate;
    }

    public void setSbaLicenceDate(LocalDate sbaRegistrationDate) {
        this.sbaLicenceExpiryDate = sbaRegistrationDate;
    }
}