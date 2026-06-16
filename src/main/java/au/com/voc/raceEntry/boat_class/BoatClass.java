package au.com.voc.raceEntry.boat_class;

import javax.persistence.*;

@Entity
@Table(name = "boat_class")
public class BoatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String className;

    protected BoatClass() {}

    public BoatClass(String className) {
        this.className = className;
    }

    public Long getId() { return id; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
