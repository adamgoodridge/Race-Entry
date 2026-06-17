package au.com.voc.raceEntry.mediator;

import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.entry.EntryStatus;
import au.com.voc.raceEntry.owner.OwnerService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RacingEventMediator {

    private final OwnerService ownerService;
    private final BoatService boatService;
    private final EntryService entryService;

    public RacingEventMediator(OwnerService ownerService, BoatService boatService, EntryService entryService) {
        this.ownerService = ownerService;
        this.boatService = boatService;
        this.entryService = entryService;
    }

    @Transactional
    public void deleteOwner(Long ownerId) {
        boatService.findByOwner(ownerId).forEach(boat -> {
            entryService.findByBoat(boat.getBoatId()).stream()
                    .filter(e -> e.getStatus() == EntryStatus.ACTIVE)
                    .forEach(e -> entryService.cancel(e.getEntryId()));
            boatService.markOrphaned(boat.getBoatId());
        });
        ownerService.delete(ownerId);
    }
}
