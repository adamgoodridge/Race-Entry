package au.com.voc.raceEntry.event;

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
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService service;

    @Test
    void create_savesEventWithStatusOpen() {
        Event saved = new Event("Regatta 2026");
        when(repository.save(any(Event.class))).thenReturn(saved);

        Event result = service.create("Regatta 2026");

        assertThat(result.getName()).isEqualTo("Regatta 2026");
        assertThat(result.getStatus()).isEqualTo(EventStatus.OPEN);
        verify(repository).save(any(Event.class));
    }

    @Test
    void findAll_returnsAllEvents() {
        when(repository.findAll()).thenReturn(List.of(
                new Event("Regatta 2026"),
                new Event("Club Race")));

        List<Event> result = service.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsEvent_whenFound() {
        Event event = new Event("Regatta 2026");
        when(repository.findById(1L)).thenReturn(Optional.of(event));

        Event result = service.findById(1L);

        assertThat(result.getName()).isEqualTo("Regatta 2026");
    }

    @Test
    void findById_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_updatesNameAndReturnsEvent_whenFound() {
        Event existing = new Event("Old Name");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Event.class))).thenReturn(existing);

        Event result = service.update(1L, "New Name");

        assertThat(result.getName()).isEqualTo("New Name");
        verify(repository).save(existing);
    }

    @Test
    void update_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, "New Name"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void close_setsStatusToClosedAndReturnsEvent_whenFound() {
        Event existing = new Event("Regatta 2026");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Event.class))).thenReturn(existing);

        Event result = service.close(1L);

        assertThat(result.getStatus()).isEqualTo(EventStatus.CLOSED);
        verify(repository).save(existing);
    }

    @Test
    void close_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.close(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesEvent_whenFound() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_throws404_whenNotFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
