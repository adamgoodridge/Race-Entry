package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.exception.BusinessRuleViolationException;
import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntryServiceTest {

    @Mock private EntryRepository entryRepository;
    @Mock private BoatRepository boatRepository;
    @Mock private EventRepository eventRepository;
    @Mock private EntryDriverRepository driverRepository;

    @InjectMocks private EntryService service;

    @Test
    void create_savesDraftEntry() {
        Entry saved = new Entry(1L, 2L);
        when(entryRepository.existsByBoatIdAndEventId(1L, 2L)).thenReturn(false);
        when(entryRepository.save(any(Entry.class))).thenReturn(saved);

        Entry result = service.create(1L, 2L);

        assertThat(result.getBoatId()).isEqualTo(1L);
        assertThat(result.getEventId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(EntryStatus.DRAFT);
        verify(entryRepository).save(any(Entry.class));
    }

    @Test
    void create_throws409_whenDuplicateBoatEvent() {
        when(entryRepository.existsByBoatIdAndEventId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> service.create(1L, 2L))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void findById_returnsEntry_whenFound() {
        Entry entry = new Entry(1L, 2L);
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        Entry result = service.findById(1L);

        assertThat(result.getBoatId()).isEqualTo(1L);
        assertThat(result.getEventId()).isEqualTo(2L);
    }

    @Test
    void findById_throws404_whenNotFound() {
        when(entryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByEventId_returnsEntriesForEvent() {
        when(entryRepository.findByEventId(2L)).thenReturn(List.of(
                new Entry(1L, 2L),
                new Entry(3L, 2L)));

        List<Entry> result = service.findByEventId(2L);

        assertThat(result).hasSize(2);
    }

    @Test
    void submit_setsStatusSubmitted_whenValid() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        Boat boat = new Boat("Speedy", "AUS1", 1L, 10L);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));
        when(boatRepository.findById(1L)).thenReturn(Optional.of(boat));
        when(driverRepository.countByEntryId(1L)).thenReturn(1L);
        when(entryRepository.save(any(Entry.class))).thenReturn(entry);

        Entry result = service.submit(1L);

        assertThat(result.getStatus()).isEqualTo(EntryStatus.SUBMITTED);
        verify(entryRepository).save(entry);
    }

    @Test
    void submit_throws422_whenEventIsClosed() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        event.setStatus(EventStatus.CLOSED);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> service.submit(1L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("closed");
    }

    @Test
    void submit_throws422_whenBoatHasNoOwner() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        Boat boat = new Boat("Speedy", "AUS1", 1L, null);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));
        when(boatRepository.findById(1L)).thenReturn(Optional.of(boat));

        assertThatThrownBy(() -> service.submit(1L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("no owner");
    }

    @Test
    void submit_throws422_whenNoDrivers() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        Boat boat = new Boat("Speedy", "AUS1", 1L, 10L);

        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(event));
        when(boatRepository.findById(1L)).thenReturn(Optional.of(boat));
        when(driverRepository.countByEntryId(1L)).thenReturn(0L);

        assertThatThrownBy(() -> service.submit(1L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("no drivers");
    }

    @Test
    void updateStatus_changesStatusAndReturnsEntry_whenFound() {
        Entry entry = new Entry(1L, 2L);
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(entryRepository.save(any(Entry.class))).thenReturn(entry);

        Entry result = service.updateStatus(1L, EntryStatus.SUBMITTED);

        assertThat(result.getStatus()).isEqualTo(EntryStatus.SUBMITTED);
        verify(entryRepository).save(entry);
    }

    @Test
    void updateStatus_throws404_whenNotFound() {
        when(entryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(99L, EntryStatus.SUBMITTED))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesEntry_whenFound() {
        when(entryRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(entryRepository).deleteById(1L);
    }

    @Test
    void delete_throws404_whenNotFound() {
        when(entryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
