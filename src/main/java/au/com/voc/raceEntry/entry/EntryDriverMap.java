package au.com.voc.raceEntry.entry;

import javax.persistence.*;

@Entity
@Table(name = "entry_driver_map")
public class EntryDriverMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mapId;

    @Column(nullable = false)
    private Long entryId;

    @Column(nullable = false)
    private Long driverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverRole role;

    protected EntryDriverMap() {}

    public EntryDriverMap(Long entryId, Long driverId, DriverRole role) {
        this.entryId = entryId;
        this.driverId = driverId;
        this.role = role;
    }

    public Long getMapId() { return mapId; }
    public Long getEntryId() { return entryId; }
    public Long getDriverId() { return driverId; }
    public DriverRole getRole() { return role; }
}
