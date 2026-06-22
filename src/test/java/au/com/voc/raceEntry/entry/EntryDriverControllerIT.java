package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
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
class EntryDriverControllerIT {

    @Autowired MockMvc mvc;
    @Autowired EntryDriverRepository entryDriverRepository;
    @Autowired EntryRepository entryRepository;
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

    private Entry savedEntry() {
        BoatClass bc = boatClassRepository.save(new BoatClass("Laser"));
        Person owner = personRepository.save(new Person("Jane", "Smith", "jane@example.com"));
        Boat boat = boatRepository.save(new Boat("Speedy", "AUS1", bc.getId(), owner.getId()));
        Event event = eventRepository.save(new Event("Spring Regatta"));
        return entryRepository.save(new Entry(boat.getId(), event.getId()));
    }

    private Person savedPerson(String email) {
        return personRepository.save(new Person("Test", "Driver", email));
    }

    @Test
    void POST_addDriver_returns_201_with_driver() throws Exception {
        Entry entry = savedEntry();
        Person driver = savedPerson("driver@example.com");

        mvc.perform(post("/api/entries/{entryId}/drivers", entry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"personId\":" + driver.getId() + ",\"role\":\"helm\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.entryId").value(entry.getId()))
                .andExpect(jsonPath("$.personId").value(driver.getId()))
                .andExpect(jsonPath("$.role").value("helm"));
    }

    @Test
    void POST_addDriver_returns_409_on_duplicate() throws Exception {
        Entry entry = savedEntry();
        Person driver = savedPerson("driver@example.com");
        entryDriverRepository.save(new EntryDriver(entry.getId(), driver.getId()));

        mvc.perform(post("/api/entries/{entryId}/drivers", entry.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"personId\":" + driver.getId() + "}"))
                .andExpect(status().isConflict());
    }

    @Test
    void GET_listDrivers_returns_200_with_list() throws Exception {
        Entry entry = savedEntry();
        Person d1 = savedPerson("d1@example.com");
        Person d2 = savedPerson("d2@example.com");
        entryDriverRepository.save(new EntryDriver(entry.getId(), d1.getId()));
        entryDriverRepository.save(new EntryDriver(entry.getId(), d2.getId()));

        mvc.perform(get("/api/entries/{entryId}/drivers", entry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void PUT_updateRole_returns_200_with_updated_role() throws Exception {
        Entry entry = savedEntry();
        Person driver = savedPerson("driver@example.com");
        EntryDriver saved = entryDriverRepository.save(new EntryDriver(entry.getId(), driver.getId()));

        mvc.perform(put("/api/entry-drivers/{id}/role", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"crew\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("crew"));
    }

    @Test
    void PUT_updateRole_returns_404_when_missing() throws Exception {
        mvc.perform(put("/api/entry-drivers/999/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"crew\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void DELETE_removeDriver_returns_204() throws Exception {
        Entry entry = savedEntry();
        Person driver = savedPerson("driver@example.com");
        EntryDriver saved = entryDriverRepository.save(new EntryDriver(entry.getId(), driver.getId()));

        mvc.perform(delete("/api/entry-drivers/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void DELETE_removeDriver_returns_404_when_missing() throws Exception {
        mvc.perform(delete("/api/entry-drivers/999"))
                .andExpect(status().isNotFound());
    }
}
