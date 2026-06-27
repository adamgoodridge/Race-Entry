package au.com.voc.raceEntry.boatclass;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "boat_class")
@Getter
@Setter
@NoArgsConstructor
public class BoatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoatClassStatus status = BoatClassStatus.ACTIVE;
}
