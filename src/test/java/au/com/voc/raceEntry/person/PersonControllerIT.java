package au.com.voc.raceEntry.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
class PersonControllerIT {

    @Autowired MockMvc mvc;
    @Autowired PersonRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void POST_creates_and_returns_201() throws Exception {
        mvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Alice\",\"lastName\":\"Smith\",\"email\":\"alice@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void GET_list_returns_all_persons() throws Exception {
        repository.saveAll(List.of(
                new Person("Alice", "Smith", "alice@example.com"),
                new Person("Bob", "Jones", "bob@example.com")
        ));

        mvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void GET_by_id_returns_200() throws Exception {
        Person saved = repository.save(new Person("Alice", "Smith", "alice@example.com"));

        mvc.perform(get("/api/persons/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void GET_by_id_returns_404_when_missing() throws Exception {
        mvc.perform(get("/api/persons/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void PUT_updates_and_returns_200() throws Exception {
        Person saved = repository.save(new Person("Alice", "Smith", "alice@example.com"));

        mvc.perform(put("/api/persons/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"Alicia\",\"lastName\":\"Jones\",\"email\":\"alicia@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alicia"))
                .andExpect(jsonPath("$.lastName").value("Jones"))
                .andExpect(jsonPath("$.email").value("alicia@example.com"));
    }

    @Test
    void PUT_returns_404_when_missing() throws Exception {
        mvc.perform(put("/api/persons/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"X\",\"lastName\":\"Y\",\"email\":\"x@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_returns_204() throws Exception {
        Person saved = repository.save(new Person("Alice", "Smith", "alice@example.com"));

        mvc.perform(delete("/api/persons/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_returns_404_when_missing() throws Exception {
        mvc.perform(delete("/api/persons/999"))
                .andExpect(status().isNotFound());
    }
}
