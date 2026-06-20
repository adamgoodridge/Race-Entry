package au.com.voc.raceEntry.person;

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
class PersonServiceTest {

    @Mock
    private PersonRepository repository;

    @InjectMocks
    private PersonService service;

    @Test
    void create_savesAndReturnsPerson() {
        Person saved = new Person("John", "Smith", "john@example.com");
        when(repository.save(any(Person.class))).thenReturn(saved);

        Person result = service.create("John", "Smith", "john@example.com");

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Smith");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(repository).save(any(Person.class));
    }

    @Test
    void findAll_returnsAllPersons() {
        when(repository.findAll()).thenReturn(List.of(
                new Person("A", "B", "a@b.com"),
                new Person("C", "D", "c@d.com")));

        List<Person> result = service.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsPerson_whenFound() {
        Person person = new Person("John", "Smith", "john@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        Person result = service.findById(1L);

        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findById_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_updatesAndReturnsPerson_whenFound() {
        Person existing = new Person("John", "Smith", "john@example.com");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Person.class))).thenReturn(existing);

        Person result = service.update(1L, "Jane", "Doe", "jane@example.com");

        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        verify(repository).save(existing);
    }

    @Test
    void update_throws404_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, "Jane", "Doe", "jane@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesPerson_whenFound() {
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
