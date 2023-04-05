package au.com.voc.raceEntry.boat_class;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "boat_class", schema = "entries")
public class BoatClass {
    @Id
    @Column(name = "class_name")
    private String className;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

}
