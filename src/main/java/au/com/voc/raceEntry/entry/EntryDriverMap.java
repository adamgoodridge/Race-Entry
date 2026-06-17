package au.com.voc.raceEntry.entry;

import javax.persistence.*;

@Entity
@Table(name = "entry_driver_map")
public class EntryDriverMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mapId;

    private Long entryId;
    private Long driverId;

    @Enumerated(EnumType.STRING)
    private DriverRole role;

    public Long getMapId() { return mapId; }
    public void setMapId(Long mapId) { this.mapId = mapId; }

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public DriverRole getRole() { return role; }
    public void setRole(DriverRole role) { this.role = role; }
}
