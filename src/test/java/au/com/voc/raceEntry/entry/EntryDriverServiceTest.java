package au.com.voc.raceEntry.entry;

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
class EntryDriverServiceTest {

    @Mock private EntryDriverRepository driverRepository;

    @InjectMocks private EntryDriverService service;

    @Test
    void addDriver_savesEntryDriver_whenValid() {
        EntryDriver saved = new EntryDriver(1L, 10L);
        saved.setRole("helm");
        when(driverRepository.existsByEntryIdAndPersonId(1L, 10L)).thenReturn(false);
        when(driverRepository.save(any(EntryDriver.class))).thenReturn(saved);

        EntryDriver result = service.addDriver(1L, 10L, "helm");

        assertThat(result.getEntryId()).isEqualTo(1L);
        assertThat(result.getPersonId()).isEqualTo(10L);
        assertThat(result.getRole()).isEqualTo("helm");
        verify(driverRepository).save(any(EntryDriver.class));
    }

    @Test
    void addDriver_throws409_whenDuplicateDriver() {
        when(driverRepository.existsByEntryIdAndPersonId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.addDriver(1L, 10L, null))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void findByEntryId_returnsDriversForEntry() {
        when(driverRepository.findByEntryId(1L)).thenReturn(List.of(
                new EntryDriver(1L, 10L),
                new EntryDriver(1L, 20L)));

        List<EntryDriver> result = service.findByEntryId(1L);

        assertThat(result).hasSize(2);
    }

    @Test
    void updateRole_updatesRoleAndReturns_whenFound() {
        EntryDriver driver = new EntryDriver(1L, 10L);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(EntryDriver.class))).thenReturn(driver);

        EntryDriver result = service.updateRole(1L, "crew");

        assertThat(result.getRole()).isEqualTo("crew");
        verify(driverRepository).save(driver);
    }

    @Test
    void updateRole_throws404_whenNotFound() {
        when(driverRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateRole(99L, "crew"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void removeDriver_deletesDriver_whenFound() {
        when(driverRepository.existsById(1L)).thenReturn(true);

        service.removeDriver(1L);

        verify(driverRepository).deleteById(1L);
    }

    @Test
    void removeDriver_throws404_whenNotFound() {
        when(driverRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.removeDriver(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
