package au.com.voc.raceEntry.boatclass;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "boat_class")
public class BoatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    protected BoatClass() {}

    public BoatClass(String name) {
        this.name = name;
    }
}
