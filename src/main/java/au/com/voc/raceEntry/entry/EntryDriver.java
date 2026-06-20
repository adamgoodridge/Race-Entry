package au.com.voc.raceEntry.entry;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "entry_driver", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"entry_id", "person_id"})
})
public class EntryDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_id", nullable = false)
    private Long entryId;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Setter
    @Column(name = "role")
    private String role;

    protected EntryDriver() {}

    public EntryDriver(Long entryId, Long personId) {
        this.entryId = entryId;
        this.personId = personId;
    }
}
