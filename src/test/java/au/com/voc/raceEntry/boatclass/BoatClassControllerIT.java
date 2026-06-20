package au.com.voc.raceEntry.boatclass;

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
class BoatClassControllerIT {

    @Autowired MockMvc mvc;
    @Autowired BoatClassRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void POST_creates_and_returns_201() throws Exception {
        mvc.perform(post("/api/boat-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Formula 4S\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Formula 4S"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void GET_list_returns_all_boat_classes() throws Exception {
        repository.saveAll(List.of(new BoatClass("A"), new BoatClass("B")));

        mvc.perform(get("/api/boat-classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void GET_by_id_returns_200() throws Exception {
        BoatClass saved = repository.save(new BoatClass("Formula 4S"));

        mvc.perform(get("/api/boat-classes/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Formula 4S"));
    }

    @Test
    void GET_by_id_returns_404_when_missing() throws Exception {
        mvc.perform(get("/api/boat-classes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_returns_204() throws Exception {
        BoatClass saved = repository.save(new BoatClass("Formula 4S"));

        mvc.perform(delete("/api/boat-classes/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_returns_404_when_missing() throws Exception {
        mvc.perform(delete("/api/boat-classes/999"))
                .andExpect(status().isNotFound());
    }
}
