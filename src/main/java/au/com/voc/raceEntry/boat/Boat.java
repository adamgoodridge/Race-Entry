package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.boat_class.BoatClass;
import au.com.voc.raceEntry.person.Person;

import javax.persistence.*;

@Entity
@Table(name = "boat")
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sailNumber;

    // Nullable: a boat can exist in the system without an owner.
    // Ownership is validated at the Entry level before race submission.
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Person owner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "boat_class_id", nullable = false)
    private BoatClass boatClass;

    protected Boat() {}

    public Boat(String name, String sailNumber, BoatClass boatClass) {
        this.name = name;
        this.sailNumber = sailNumber;
        this.boatClass = boatClass;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSailNumber() { return sailNumber; }
    public Person getOwner() { return owner; }
    public BoatClass getBoatClass() { return boatClass; }

    public void setName(String name) { this.name = name; }
    public void setSailNumber(String sailNumber) { this.sailNumber = sailNumber; }
    public void setOwner(Person owner) { this.owner = owner; }
    public void setBoatClass(BoatClass boatClass) { this.boatClass = boatClass; }

    public boolean hasOwner() { return owner != null; }
}
