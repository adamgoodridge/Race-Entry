package au.com.voc.raceEntry.boatclass;

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
class BoatClassServiceTest {

    @Mock
    private BoatClassRepository repository;

    @InjectMocks
    private BoatClassService service;

    @Test
    void create_savesAndReturnsBoatClass() {
        BoatClass saved = new BoatClass("Formula 4S");
        when(repository.save(any(BoatClass.class))).thenReturn(saved);

        BoatClass result = service.create("Formula 4S");

        assertThat(result.getName()).isEqualTo("Formula 4S");
        verify(repository).save(any(BoatClass.class));
    }

    @Test
    void findAll_returnsAllBoatClasses() {
        when(repository.findAll()).thenReturn(List.of(new BoatClass("A"), new BoatClass("B")));

        List<BoatClass> result = service.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsBoatClass_whenFound() {
        BoatClass bc = new BoatClass("Formula 4S");
        when(repository.findById(1L)).thenReturn(Optional.of(bc));

        BoatClass result = service.findById(1L);

        assertThat(result.getName()).isEqualTo("Formula 4S");
    }

    @Test
    void findById_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesBoatClass_whenFound() {
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
