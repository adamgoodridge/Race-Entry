package au.com.voc.raceEntry.person;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;
    private String phone;
    private String streetAddress;
    private String suburb;
    private String state;
    private String postcode;
    private String sbaLicenceNumber;
    private LocalDate sbaExpiryDate;
    private Long userId;
}
