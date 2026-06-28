package au.com.voc.raceEntry.entry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntryRequest {
    private Long boatId;
    private Long eventId;
    private Long boatClassId;
}
