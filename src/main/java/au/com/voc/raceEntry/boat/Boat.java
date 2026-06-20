package au.com.voc.raceEntry.boat;

import javax.persistence.*;

@Entity
@Table(name = "boat")
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sailNumber;

    @Column(nullable = false)
    private Long boatClassId;

    @Column
    private Long ownerId;

    protected Boat() {}

    public Boat(String name, String sailNumber, Long boatClassId, Long ownerId) {
        this.name = name;
        this.sailNumber = sailNumber;
        this.boatClassId = boatClassId;
        this.ownerId = ownerId;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSailNumber() { return sailNumber; }
    public void setSailNumber(String sailNumber) { this.sailNumber = sailNumber; }

    public Long getBoatClassId() { return boatClassId; }
    public void setBoatClassId(Long boatClassId) { this.boatClassId = boatClassId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
