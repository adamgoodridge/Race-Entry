package au.com.voc.raceEntry.boatclass;

import javax.persistence.*;

@Entity
@Table(name = "boat_class")
public class BoatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected BoatClass() {}

    public BoatClass(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
