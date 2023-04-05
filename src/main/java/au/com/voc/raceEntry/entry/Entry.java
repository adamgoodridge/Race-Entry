package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.user.User;
import com.sun.istack.NotNull;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;

@Entity
public class Entry {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "entry_id")
    private long entryId;
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "boat_id")
    @NotNull
    private Boat boat;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @NotNull
    @JoinColumn(name = "driver_1_id")
    private Person driverOne;


    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "driver_2_id")
    private Person driverTwo;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "boat_class")
    private String boatClass;

    @ColumnTransformer(
            read = "AES_DECRYPT(declaration_owner_signature, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "declaration_owner_signature")
    private String declarationOwnerSignature;
    @ColumnTransformer(
            read = "AES_DECRYPT(declaration_owner_date, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "declaration_owner_date")
    private String declarationOwnerDate;
    @ColumnTransformer(
            read = "AES_DECRYPT(declaration_driver_one_signature, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "declaration_driver_one_signature")
    private String declarationDriverOneSignature;
    @ColumnTransformer(
            read = "AES_DECRYPT(declaration_driver_one_date, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "declaration_driver_one_date")
    private String declarationDriverOneDate;

    @ColumnTransformer(
            read = "AES_DECRYPT(declaration_driver_two_signature, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "declaration_driver_two_signature")
    private String declarationDriverTwoSignature;
    @ColumnTransformer(
            read = "AES_DECRYPT(declaration_driver_two_date, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "declaration_driver_two_date")
    private String declarationDriverTwoDate;
    //not a column
    @Transient
    private String previousUrl;

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

    public Person getPersonOne() {
        return driverOne;
    }

    public void setPersonOne(Person firstPersonId) {
        this.driverOne = firstPersonId;
    }

    public Person getPersonTwo() {
        return driverTwo;
    }

    public void setPersonTwo(Person secondPersonId) {
        this.driverTwo = secondPersonId;
    }

    public Person getDriverOne() {
        return driverOne;
    }

    public void setDriverOne(Person driverOne) {
        this.driverOne = driverOne;
    }

    public Person getDriverTwo() {
        return driverTwo;
    }

    public void setDriverTwo(Person driverTwo) {
        this.driverTwo = driverTwo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBoatClass() {
        return boatClass;
    }

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public void setBoatClass(String boatClass) {
        this.boatClass = boatClass;
    }

    public String getDeclarationOwnerSignature() {
        return declarationOwnerSignature;
    }

    public void setDeclarationOwnerSignature(String declarationOwnerSignature) {
        this.declarationOwnerSignature = declarationOwnerSignature;
    }

    public String getDeclarationOwnerDate() {
        return declarationOwnerDate;
    }

    public void setDeclarationOwnerDate(String declarationOwnerDate) {
        this.declarationOwnerDate = declarationOwnerDate;
    }

    public String getDeclarationDriverOneSignature() {
        return declarationDriverOneSignature;
    }

    public void setDeclarationDriverOneSignature(String declarationDriverOneSignature) {
        this.declarationDriverOneSignature = declarationDriverOneSignature;
    }

    public String getDeclarationDriverOneDate() {
        return declarationDriverOneDate;
    }

    public void setDeclarationDriverOneDate(String declarationDriverOneDate) {
        this.declarationDriverOneDate = declarationDriverOneDate;
    }

    public String getDeclarationDriverTwoSignature() {
        return declarationDriverTwoSignature;
    }

    public void setDeclarationDriverTwoSignature(String declarationDriverTwoSignature) {
        this.declarationDriverTwoSignature = declarationDriverTwoSignature;
    }

    public String getDeclarationDriverTwoDate() {
        return declarationDriverTwoDate;
    }

    public void setDeclarationDriverTwoDate(String declarationDriverTwoDate) {
        this.declarationDriverTwoDate = declarationDriverTwoDate;
    }

    //used in entries by event
    public String summaryLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(boat.getName());
        sb.append(" (");
        sb.append(driverOne.getFullName());
        if (driverTwo != null) {
            sb.append(" & ");
            sb.append(driverTwo.getFullName());
        }
        sb.append(")");
        return sb.toString();
    }

    public String getPersonsNames() {
        String output = driverOne.getFullName();
        if (driverTwo != null) {
            output += ", " + driverTwo.getFullName();
        }
        return output;
    }
}


