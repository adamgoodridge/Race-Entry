package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventStatus;
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
class EntryControllerIT {

    @Autowired MockMvc mvc;
    @Autowired EntryRepository entryRepository;
    @Autowired EntryDriverRepository entryDriverRepository;
    @Autowired BoatRepository boatRepository;
    @Autowired EventRepository eventRepository;
    @Autowired PersonRepository personRepository;
    @Autowired BoatClassRepository boatClassRepository;

    @BeforeEach
    void cleanUp() {
        entryDriverRepository.deleteAll();
        entryRepository.deleteAll();
        boatRepository.deleteAll();
        eventRepository.deleteAll();
        personRepository.deleteAll();
        boatClassRepository.deleteAll();
    }

    private Boat savedBoatWithOwner() {
        BoatClass bc = boatClassRepository.save(new BoatClass("Laser"));
        Person owner = personRepository.save(new Person("Jane", "Smith", "jane@example.com"));
        return boatRepository.save(new Boat("Speedy", "AUS1", bc.getId(), owner.getId()));
    }

    private Boat savedBoatWithoutOwner() {
        BoatClass bc = boatClassRepository.save(new BoatClass("Laser"));
        return boatRepository.save(new Boat("Speedy", "AUS1", bc.getId(), null));
    }

    @Test
    void POST_creates_draft_entry_and_returns_201() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));

        mvc.perform(post("/api/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boatId\":" + boat.getId() + ",\"eventId\":" + event.getId() + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.boatId").value(boat.getId()))
                .andExpect(jsonPath("$.eventId").value(event.getId()))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void POST_returns_409_on_duplicate_boat_event() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(post("/api/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"boatId\":" + boat.getId() + ",\"eventId\":" + event.getId() + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    void GET_list_by_event_returns_entries() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(get("/api/entries/event/{eventId}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void GET_by_id_returns_200() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        Entry entry = entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(get("/api/entries/{id}", entry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entry.getId()))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void GET_by_id_returns_404_when_missing() throws Exception {
        mvc.perform(get("/api/entries/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void POST_submit_returns_200_and_status_SUBMITTED() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        Entry entry = entryRepository.save(new Entry(boat.getId(), event.getId()));
        entryDriverRepository.save(new EntryDriver(entry.getId(), boat.getOwnerId()));

        mvc.perform(post("/api/entries/{id}/submit", entry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void POST_submit_returns_422_when_event_is_closed() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        event.setStatus(EventStatus.CLOSED);
        eventRepository.save(event);
        Entry entry = entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(post("/api/entries/{id}/submit", entry.getId()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void POST_submit_returns_422_when_boat_has_no_owner() throws Exception {
        Boat boat = savedBoatWithoutOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        Entry entry = entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(post("/api/entries/{id}/submit", entry.getId()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void POST_submit_returns_422_when_no_drivers() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        Entry entry = entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(post("/api/entries/{id}/submit", entry.getId()))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void POST_submit_returns_404_when_entry_missing() throws Exception {
        mvc.perform(post("/api/entries/999/submit"))
                .andExpect(status().isNotFound());
    }

    @Test
    void PUT_status_updates_and_returns_200() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        Entry entry = entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(put("/api/entries/{id}/status", entry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SUBMITTED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void PUT_status_returns_404_when_missing() throws Exception {
        mvc.perform(put("/api/entries/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SUBMITTED\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_returns_204() throws Exception {
        Boat boat = savedBoatWithOwner();
        Event event = eventRepository.save(new Event("Spring Regatta"));
        Entry entry = entryRepository.save(new Entry(boat.getId(), event.getId()));

        mvc.perform(delete("/api/entries/{id}", entry.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_returns_404_when_missing() throws Exception {
        mvc.perform(delete("/api/entries/999"))
                .andExpect(status().isNotFound());
    }
}
