package au.com.voc.raceEntry.entry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "entry_driver", uniqueConstraints = @UniqueConstraint(columnNames = {"entry_id", "person_id"}))
@Getter
@Setter
@NoArgsConstructor
public class EntryDriver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_id", nullable = false)
    private Long entryId;

    @Column(name = "person_id", nullable = false)
    private Long personId;
}
