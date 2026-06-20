package au.com.voc.raceEntry.person;

import lombok.Data;

@Data
public class PersonRequest {
    private String firstName;
    private String lastName;
    private String email;
}
