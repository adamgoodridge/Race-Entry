package com.voc.raceEntry.Service;

////import com.voc.raceEntry.Entity.Entyry;
import com.voc.raceEntry.Entity.Entry;
import com.voc.raceEntry.Entity.EntryFormData;

import java.io.IOException;
import java.util.List;

public interface EntryService {
    public void updateEntry(EntryFormData entryFormData);
    public Entry getEntry(Long id);
    public List<Entry> getEntriesByEvent(Long eventId);
    public byte[] generatePDF(Entry entry) throws IOException;
}
