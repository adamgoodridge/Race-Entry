package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.person.Person;

import javax.persistence.*;

@Entity
@Table(
    name = "entry_driver",
    uniqueConstraints = @UniqueConstraint(columnNames = {"entry_id", "driver_id"})
)
public class EntryDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "entry_id", nullable = false)
    private Entry entry;

    @ManyToOne(optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Person driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverRole role;

    protected EntryDriver() {}

    public EntryDriver(Entry entry, Person driver, DriverRole role) {
        this.entry = entry;
        this.driver = driver;
        this.role = role;
    }

    public Long getId() { return id; }
    public Entry getEntry() { return entry; }
    public Person getDriver() { return driver; }
    public DriverRole getRole() { return role; }

    public void setRole(DriverRole role) { this.role = role; }
}
