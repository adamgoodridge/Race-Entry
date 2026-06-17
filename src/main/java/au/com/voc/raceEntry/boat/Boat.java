package au.com.voc.raceEntry.boat;

import javax.persistence.*;

@Entity
@Table(name = "boat")
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boatId;

    private Long ownerId;
    private String name;

    @Column(name = "boat_class")
    private String boatClass;

    public Long getBoatId() { return boatId; }
    public void setBoatId(Long boatId) { this.boatId = boatId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBoatClass() { return boatClass; }
    public void setBoatClass(String boatClass) { this.boatClass = boatClass; }
}
