package au.com.voc.raceEntry.event;

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
class EventControllerIT {

    @Autowired MockMvc mvc;
    @Autowired EventRepository eventRepository;

    @BeforeEach
    void cleanUp() {
        eventRepository.deleteAll();
    }

    @Test
    void POST_creates_and_returns_201() throws Exception {
        mvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Spring Regatta\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Spring Regatta"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void GET_list_returns_all_events() throws Exception {
        eventRepository.saveAll(List.of(
                new Event("Spring Regatta"),
                new Event("Autumn Series")
        ));

        mvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void GET_by_id_returns_200() throws Exception {
        Event saved = eventRepository.save(new Event("Spring Regatta"));

        mvc.perform(get("/api/events/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Regatta"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void GET_by_id_returns_404_when_missing() throws Exception {
        mvc.perform(get("/api/events/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void PUT_updates_name_and_returns_200() throws Exception {
        Event saved = eventRepository.save(new Event("Spring Regatta"));

        mvc.perform(put("/api/events/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Autumn Series\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Autumn Series"));
    }

    @Test
    void PUT_returns_404_when_missing() throws Exception {
        mvc.perform(put("/api/events/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"X\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void PUT_close_sets_status_to_CLOSED_and_returns_200() throws Exception {
        Event saved = eventRepository.save(new Event("Spring Regatta"));

        mvc.perform(put("/api/events/{id}/close", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void PUT_close_returns_404_when_missing() throws Exception {
        mvc.perform(put("/api/events/999/close"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_returns_204() throws Exception {
        Event saved = eventRepository.save(new Event("Spring Regatta"));

        mvc.perform(delete("/api/events/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_returns_404_when_missing() throws Exception {
        mvc.perform(delete("/api/events/999"))
                .andExpect(status().isNotFound());
    }
}
