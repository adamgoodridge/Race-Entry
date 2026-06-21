package au.com.voc.raceEntry.mediator;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryDriverService;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.entry.EntryStatus;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventService;
import au.com.voc.raceEntry.event.EventStatus;
import au.com.voc.raceEntry.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RaceEntryMediatorTest {

    @Mock private EntryService entryService;
    @Mock private BoatService boatService;
    @Mock private EventService eventService;
    @Mock private EntryDriverService entryDriverService;

    @InjectMocks private RaceEntryMediator mediator;

    @Test
    void submit_setsStatusSubmitted_whenValid() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        Boat boat = new Boat("Speedy", "AUS1", 1L, 10L);
        Entry submitted = new Entry(1L, 2L);
        submitted.setStatus(EntryStatus.SUBMITTED);

        when(entryService.findById(1L)).thenReturn(entry);
        when(eventService.findById(2L)).thenReturn(event);
        when(boatService.findById(1L)).thenReturn(boat);
        when(entryDriverService.countByEntryId(1L)).thenReturn(1L);
        when(entryService.updateStatus(eq(1L), eq(EntryStatus.SUBMITTED))).thenReturn(submitted);

        Entry result = mediator.submit(1L);

        assertThat(result.getStatus()).isEqualTo(EntryStatus.SUBMITTED);
        verify(entryService).updateStatus(1L, EntryStatus.SUBMITTED);
    }

    @Test
    void submit_throws422_whenEventIsClosed() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        event.setStatus(EventStatus.CLOSED);

        when(entryService.findById(1L)).thenReturn(entry);
        when(eventService.findById(2L)).thenReturn(event);

        assertThatThrownBy(() -> mediator.submit(1L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("closed");
    }

    @Test
    void submit_throws422_whenBoatHasNoOwner() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        Boat boat = new Boat("Speedy", "AUS1", 1L, null);

        when(entryService.findById(1L)).thenReturn(entry);
        when(eventService.findById(2L)).thenReturn(event);
        when(boatService.findById(1L)).thenReturn(boat);

        assertThatThrownBy(() -> mediator.submit(1L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("no owner");
    }

    @Test
    void submit_throws422_whenNoDrivers() {
        Entry entry = new Entry(1L, 2L);
        Event event = new Event("Regatta");
        Boat boat = new Boat("Speedy", "AUS1", 1L, 10L);

        when(entryService.findById(1L)).thenReturn(entry);
        when(eventService.findById(2L)).thenReturn(event);
        when(boatService.findById(1L)).thenReturn(boat);
        when(entryDriverService.countByEntryId(1L)).thenReturn(0L);

        assertThatThrownBy(() -> mediator.submit(1L))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("no drivers");
    }
}
