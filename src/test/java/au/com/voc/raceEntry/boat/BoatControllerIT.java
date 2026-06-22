package au.com.voc.raceEntry.boat;

import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "connection.driver_class=",
        "connection.pool.initialPoolSize=",
        "connection.pool.minPoolSize=",
        "connection.pool.maxPoolSize=",
        "connection.pool.maxIdleTime="
})
class BoatControllerIT {

    @Autowired MockMvc mvc;
    @Autowired BoatRepository boatRepository;
    @Autowired PersonRepository personRepository;

    private Long boatClassId = 1L;

    @BeforeEach
    void cleanUp() {
        boatRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    void POST_creates_and_returns_201() throws Exception {
        mvc.perform(post("/api/boats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Wind Dancer\",\"sailNumber\":\"AUS123\",\"boatClassId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Wind Dancer"))
                .andExpect(jsonPath("$.sailNumber").value("AUS123"))
                .andExpect(jsonPath("$.boatClassId").value(1))
                .andExpect(jsonPath("$.ownerId").doesNotExist());
    }

    @Test
    void GET_list_returns_all_boats() throws Exception {
        boatRepository.saveAll(List.of(
                new Boat("Wind Dancer", "AUS123", boatClassId, null),
                new Boat("Sea Sprite", "AUS456", boatClassId, null)
        ));

        mvc.perform(get("/api/boats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void GET_list_by_owner_filters_results() throws Exception {
        Person owner = personRepository.save(new Person("Alice", "Smith", "alice@example.com"));
        boatRepository.saveAll(List.of(
                new Boat("Wind Dancer", "AUS123", boatClassId, owner.getId()),
                new Boat("Sea Sprite", "AUS456", boatClassId, null)
        ));

        mvc.perform(get("/api/boats").param("ownerId", owner.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Wind Dancer"));
    }

    @Test
    void GET_by_id_returns_200() throws Exception {
        Boat saved = boatRepository.save(new Boat("Wind Dancer", "AUS123", boatClassId, null));

        mvc.perform(get("/api/boats/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Wind Dancer"))
                .andExpect(jsonPath("$.sailNumber").value("AUS123"));
    }

    @Test
    void GET_by_id_returns_404_when_missing() throws Exception {
        mvc.perform(get("/api/boats/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void PUT_updates_and_returns_200() throws Exception {
        Boat saved = boatRepository.save(new Boat("Wind Dancer", "AUS123", boatClassId, null));

        mvc.perform(put("/api/boats/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sea Sprite\",\"sailNumber\":\"AUS456\",\"boatClassId\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sea Sprite"))
                .andExpect(jsonPath("$.sailNumber").value("AUS456"))
                .andExpect(jsonPath("$.boatClassId").value(2));
    }

    @Test
    void PUT_returns_404_when_missing() throws Exception {
        mvc.perform(put("/api/boats/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\",\"sailNumber\":\"Y\",\"boatClassId\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void PUT_assign_owner_returns_200() throws Exception {
        Boat saved = boatRepository.save(new Boat("Wind Dancer", "AUS123", boatClassId, null));
        Person owner = personRepository.save(new Person("Alice", "Smith", "alice@example.com"));

        mvc.perform(put("/api/boats/{id}/owner/{personId}", saved.getId(), owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(owner.getId()));
    }

    @Test
    void PUT_assign_owner_returns_404_when_boat_missing() throws Exception {
        mvc.perform(put("/api/boats/999/owner/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_owner_returns_200_and_clears_owner() throws Exception {
        Person owner = personRepository.save(new Person("Alice", "Smith", "alice@example.com"));
        Boat saved = boatRepository.save(new Boat("Wind Dancer", "AUS123", boatClassId, owner.getId()));

        mvc.perform(delete("/api/boats/{id}/owner", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").doesNotExist());
    }

    @Test
    void DELETE_owner_returns_404_when_boat_missing() throws Exception {
        mvc.perform(delete("/api/boats/999/owner"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_returns_204() throws Exception {
        Boat saved = boatRepository.save(new Boat("Wind Dancer", "AUS123", boatClassId, null));

        mvc.perform(delete("/api/boats/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_returns_404_when_missing() throws Exception {
        mvc.perform(delete("/api/boats/999"))
                .andExpect(status().isNotFound());
    }
}
