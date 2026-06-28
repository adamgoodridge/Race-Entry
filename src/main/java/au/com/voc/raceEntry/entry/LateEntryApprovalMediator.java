package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.exception.BusinessRuleViolationException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LateEntryApprovalMediator {

    private final EntryRepository entryRepository;

    public LateEntryApprovalMediator(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    public Entry lateApprove(Long entryId) {
        Entry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry not found: " + entryId));
        if (entry.getStatus() != EntryStatus.DRAFT) {
            throw new BusinessRuleViolationException("Late approval is only available for DRAFT entries");
        }
        entry.setStatus(EntryStatus.APPROVED);
        return entryRepository.save(entry);
    }
}
