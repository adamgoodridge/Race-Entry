package au.com.voc.raceEntry.person;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PersonRequest {
    private String firstName;
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
