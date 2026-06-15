//demo only
package au.com.voc.raceEntry.boat_class;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "boat_class", schema = "entries")
public class BoatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boatClassId;

    @Column(name = "class_name", unique = true)
    private String className;

    public Long getBoatClassId() {
        return boatClassId;
    }

    public void setBoatClassId(Long boatClassId) {
        this.boatClassId = boatClassId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
