package au.com.voc.raceEntry.boat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "boat")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String sailNumber;

    @Setter
    @Column(nullable = false)
    private Long boatClassId;

    @Setter
    @Column
    private Long ownerId;

    protected Boat() {}

    public Boat(String name, String sailNumber, Long boatClassId, Long ownerId) {
        this.name = name;
        this.sailNumber = sailNumber;
        this.boatClassId = boatClassId;
        this.ownerId = ownerId;
    }
}
