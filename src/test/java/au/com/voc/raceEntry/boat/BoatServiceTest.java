package au.com.voc.raceEntry.boat;

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
class BoatServiceTest {

    @Mock
    private BoatRepository repository;

    @InjectMocks
    private BoatService service;

    private BoatRequest request(String name, String sailNumber, Long boatClassId, Long ownerId) {
        BoatRequest req = new BoatRequest();
        req.setName(name);
        req.setSailNumber(sailNumber);
        req.setBoatClassId(boatClassId);
        req.setOwnerId(ownerId);
        return req;
    }

    @Test
    void create_withOwner_savesAndReturnsBoat() {
        Boat saved = new Boat("Speedy", "AUS123", 1L, 10L);
        when(repository.save(any(Boat.class))).thenReturn(saved);

        Boat result = service.create(request("Speedy", "AUS123", 1L, 10L));

        assertThat(result.getName()).isEqualTo("Speedy");
        assertThat(result.getSailNumber()).isEqualTo("AUS123");
        assertThat(result.getBoatClassId()).isEqualTo(1L);
        assertThat(result.getOwnerId()).isEqualTo(10L);
        verify(repository).save(any(Boat.class));
    }

    @Test
    void create_withoutOwner_savesAndReturnsBoat() {
        Boat saved = new Boat("Speedy", "AUS123", 1L, null);
        when(repository.save(any(Boat.class))).thenReturn(saved);

        Boat result = service.create(request("Speedy", "AUS123", 1L, null));

        assertThat(result.getOwnerId()).isNull();
        verify(repository).save(any(Boat.class));
    }

    @Test
    void findAll_returnsAllBoats() {
        when(repository.findAll()).thenReturn(List.of(
                new Boat("A", "AUS1", 1L, null),
                new Boat("B", "AUS2", 2L, 5L)));

        List<Boat> result = service.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsBoat_whenFound() {
        Boat boat = new Boat("Speedy", "AUS123", 1L, 10L);
        when(repository.findById(1L)).thenReturn(Optional.of(boat));

        Boat result = service.findById(1L);

        assertThat(result.getName()).isEqualTo("Speedy");
    }

    @Test
    void findById_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_updatesAndReturnsBoat_whenFound() {
        Boat existing = new Boat("Speedy", "AUS123", 1L, 10L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Boat.class))).thenReturn(existing);

        Boat result = service.update(1L, request("Faster", "AUS999", 2L, null));

        assertThat(result.getName()).isEqualTo("Faster");
        assertThat(result.getSailNumber()).isEqualTo("AUS999");
        assertThat(result.getBoatClassId()).isEqualTo(2L);
        verify(repository).save(existing);
    }

    @Test
    void update_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        BoatRequest req = request("Faster", "AUS999", 2L, null);
        assertThatThrownBy(() -> service.update(99L, req))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void assignOwner_setsOwnerIdAndReturnsBoat_whenFound() {
        Boat existing = new Boat("Speedy", "AUS123", 1L, null);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Boat.class))).thenReturn(existing);

        Boat result = service.assignOwner(1L, 42L);

        assertThat(result.getOwnerId()).isEqualTo(42L);
        verify(repository).save(existing);
    }

    @Test
    void assignOwner_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignOwner(99L, 42L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void removeOwner_clearsOwnerIdAndReturnsBoat_whenFound() {
        Boat existing = new Boat("Speedy", "AUS123", 1L, 10L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Boat.class))).thenReturn(existing);

        Boat result = service.removeOwner(1L);

        assertThat(result.getOwnerId()).isNull();
        verify(repository).save(existing);
    }

    @Test
    void removeOwner_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeOwner(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesBoat_whenFound() {
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
