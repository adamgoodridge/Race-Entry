package au.com.voc.raceEntry.boat;

import lombok.Data;

@Data
public class BoatRequest {
    private String name;
    private String sailNumber;
    private Long boatClassId;
    private Long ownerId;
}
