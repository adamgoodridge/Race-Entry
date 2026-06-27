package au.com.voc.raceEntry.boat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "boat")
@Getter
@Setter
@NoArgsConstructor
public class Boat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String raceNumber;

    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoatStatus status = BoatStatus.ACTIVE;
}
