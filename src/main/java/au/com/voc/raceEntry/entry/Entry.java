package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat_class.BoatClass;
import au.com.voc.raceEntry.event.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(
    name = "entry",
    uniqueConstraints = @UniqueConstraint(columnNames = {"boat_id", "event_id"})
)
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "boat_id", nullable = false)
    private Boat boat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status;

    @ManyToMany
    @JoinTable(
        name = "entry_boat_class",
        joinColumns = @JoinColumn(name = "entry_id"),
        inverseJoinColumns = @JoinColumn(name = "boat_class_id")
    )
    private List<BoatClass> boatClasses = new ArrayList<>();

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntryDriver> drivers = new ArrayList<>();

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntryDeclaration> declarations = new ArrayList<>();

    protected Entry() {}

    public Entry(Boat boat, Event event) {
        this.boat = boat;
        this.event = event;
        this.status = EntryStatus.DRAFT;
    }

    public Long getId() { return id; }
    public Boat getBoat() { return boat; }
    public Event getEvent() { return event; }
    public EntryStatus getStatus() { return status; }
    public List<BoatClass> getBoatClasses() { return Collections.unmodifiableList(boatClasses); }
    public List<EntryDriver> getDrivers() { return Collections.unmodifiableList(drivers); }
    public List<EntryDeclaration> getDeclarations() { return Collections.unmodifiableList(declarations); }

    public void setStatus(EntryStatus status) { this.status = status; }
    public void setBoatClasses(List<BoatClass> boatClasses) { this.boatClasses = new ArrayList<>(boatClasses); }

    void addDriver(EntryDriver driver) { drivers.add(driver); }
    void clearDrivers() { drivers.clear(); }
    void addDeclaration(EntryDeclaration declaration) { declarations.add(declaration); }
    void clearDeclarations() { declarations.clear(); }

    public boolean isReadyToSubmit() {
        return boat.hasOwner() && !drivers.isEmpty() && !boatClasses.isEmpty();
    }
}
