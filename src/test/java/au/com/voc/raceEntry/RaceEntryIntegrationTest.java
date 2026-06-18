package au.com.voc.raceEntry;

import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.driver.DriverRepository;
import au.com.voc.raceEntry.entry.EntryDriverRepository;
import au.com.voc.raceEntry.entry.EntryRepository;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.owner.OwnerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
                "server.ssl.enabled=false",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,"
                        + "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration",
                "encryption.key=test-key",
                "spring.jackson.serialization.write-dates-as-timestamps=false",
                "spring.mail.host=localhost"
        }
)
@AutoConfigureMockMvc
class RaceEntryIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private BoatRepository boatRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private EntryRepository entryRepository;
    @Autowired private EntryDriverRepository entryDriverRepository;
    @Autowired private DriverRepository driverRepository;

    @BeforeEach
    void clearDatabase() {
        entryDriverRepository.deleteAll();
        entryRepository.deleteAll();
        boatRepository.deleteAll();
        driverRepository.deleteAll();
        eventRepository.deleteAll();
        ownerRepository.deleteAll();
    }

    // ---- Owner ----

    @Test
    void createOwner_returns201() throws Exception {
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"contactEmail\":\"alice@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerId").isNumber())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.contactEmail").value("alice@example.com"));
    }

    @Test
    void deleteOwner_returns204() throws Exception {
        Long ownerId = createOwner("Bob");
        mockMvc.perform(delete("/owners/" + ownerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteOwner_notFound_returns404WithErrorBody() throws Exception {
        mockMvc.perform(delete("/owners/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }

    // ---- Boat ----

    @Test
    void registerBoat_returns201() throws Exception {
        Long ownerId = createOwner("Charlie");
        mockMvc.perform(post("/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ownerId\":" + ownerId + ",\"name\":\"Sea Hawk\",\"boatClass\":\"Laser\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boatId").isNumber())
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.name").value("Sea Hawk"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void listBoatsByOwner_returns200() throws Exception {
        Long ownerId = createOwner("Diana");
        createBoat(ownerId, "Wind Dancer", "Optimist");
        mockMvc.perform(get("/boats?ownerId=" + ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].boat.name").value("Wind Dancer"))
                .andExpect(jsonPath("$[0].owner.ownerId").value(ownerId))
                .andExpect(jsonPath("$[0].activeEntry").isEmpty());
    }

    @Test
    void registerBoat_ownerNotFound_returns404() throws Exception {
        mockMvc.perform(post("/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ownerId\":99999,\"name\":\"Ghost\",\"boatClass\":\"X\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ---- Event ----

    @Test
    void createEvent_returns201() throws Exception {
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Spring Regatta\",\"date\":\"2026-07-01\",\"location\":\"Sydney Harbour\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").isNumber())
                .andExpect(jsonPath("$.name").value("Spring Regatta"))
                .andExpect(jsonPath("$.location").value("Sydney Harbour"));
    }

    @Test
    void listEvents_returns200() throws Exception {
        createEvent("Autumn Cup");
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Autumn Cup"));
    }

    // ---- Entry ----

    @Test
    void createEntry_returns201() throws Exception {
        Long ownerId = createOwner("Eve");
        Long boatId = createBoat(ownerId, "Swift", "470");
        Long eventId = createEvent("Summer Series");
        mockMvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"boatId\":" + boatId + ",\"eventId\":" + eventId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entryId").isNumber())
                .andExpect(jsonPath("$.boatId").value(boatId))
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void listEntriesByEvent_returns200() throws Exception {
        Long ownerId = createOwner("Frank");
        Long boatId = createBoat(ownerId, "Breeze", "Laser");
        Long eventId = createEvent("Winter Cup");
        createEntry(boatId, eventId);
        mockMvc.perform(get("/entries?eventId=" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entry.boatId").value(boatId))
                .andExpect(jsonPath("$[0].boat.boatId").value(boatId))
                .andExpect(jsonPath("$[0].drivers").isArray());
    }

    @Test
    void listEntriesByBoat_returns200() throws Exception {
        Long ownerId = createOwner("Grace");
        Long boatId = createBoat(ownerId, "Gust", "Finn");
        Long eventId = createEvent("Spring Cup");
        createEntry(boatId, eventId);
        mockMvc.perform(get("/entries?boatId=" + boatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].entry.eventId").value(eventId))
                .andExpect(jsonPath("$[0].boat.boatId").value(boatId))
                .andExpect(jsonPath("$[0].drivers").isArray());
    }

    @Test
    void listEntriesByEvent_unknownEvent_returns404() throws Exception {
        mockMvc.perform(get("/entries?eventId=99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void listEntriesByBoat_unknownBoat_returns404() throws Exception {
        mockMvc.perform(get("/entries?boatId=99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void updateEntryStatus_toCancelled_returns200() throws Exception {
        Long ownerId = createOwner("Hank");
        Long boatId = createBoat(ownerId, "Ripple", "Snipe");
        Long eventId = createEvent("Fall Race");
        Long entryId = createEntry(boatId, eventId);
        mockMvc.perform(patch("/entries/" + entryId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void updateEntryStatus_toActive_withDriver_returns200() throws Exception {
        Long ownerId = createOwner("Isla");
        Long boatId = createBoat(ownerId, "Arrow", "Laser");
        Long eventId = createEvent("Day Race");
        Long entryId = createEntry(boatId, eventId);
        Long driverId = createDriver("Jake", "LIC-A01");
        assignDriver(entryId, driverId, "HELMSMAN");
        mockMvc.perform(patch("/entries/" + entryId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void createDuplicateEntry_returns409WithErrorBody() throws Exception {
        Long ownerId = createOwner("Iris");
        Long boatId = createBoat(ownerId, "Dart", "J24");
        Long eventId = createEvent("Championship");
        createEntry(boatId, eventId);
        mockMvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"boatId\":" + boatId + ",\"eventId\":" + eventId + "}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void updateEntryStatus_toActive_withoutDrivers_returns409() throws Exception {
        Long ownerId = createOwner("Jay");
        Long boatId = createBoat(ownerId, "Kite", "Moth");
        Long eventId = createEvent("Night Series");
        Long entryId = createEntry(boatId, eventId);
        mockMvc.perform(patch("/entries/" + entryId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // ---- Driver ----

    @Test
    void createDriver_duplicateLicense_returns409() throws Exception {
        mockMvc.perform(post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"First\",\"licenseNumber\":\"DUP-001\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Second\",\"licenseNumber\":\"DUP-001\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void createDriver_returns201() throws Exception {
        mockMvc.perform(post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Jack\",\"licenseNumber\":\"LIC-001\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverId").isNumber())
                .andExpect(jsonPath("$.name").value("Jack"))
                .andExpect(jsonPath("$.licenseNumber").value("LIC-001"));
    }

    @Test
    void assignDriverToEntry_returns201() throws Exception {
        Long ownerId = createOwner("Karen");
        Long boatId = createBoat(ownerId, "Flash", "Laser");
        Long eventId = createEvent("Night Race");
        Long entryId = createEntry(boatId, eventId);
        Long driverId = createDriver("Leo", "LIC-002");
        mockMvc.perform(post("/entries/" + entryId + "/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"driverId\":" + driverId + ",\"role\":\"HELMSMAN\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entryId").value(entryId))
                .andExpect(jsonPath("$.driverId").value(driverId))
                .andExpect(jsonPath("$.role").value("HELMSMAN"));
    }

    @Test
    void listDriversForEntry_returns200() throws Exception {
        Long ownerId = createOwner("Mike");
        Long boatId = createBoat(ownerId, "Thunder", "Skiff");
        Long eventId = createEvent("Grand Prix");
        Long entryId = createEntry(boatId, eventId);
        Long driverId = createDriver("Nina", "LIC-003");
        assignDriver(entryId, driverId, "CREW");
        mockMvc.perform(get("/entries/" + entryId + "/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].driverId").value(driverId))
                .andExpect(jsonPath("$[0].name").value("Nina"));
    }

    @Test
    void removeDriverFromEntry_returns204() throws Exception {
        Long ownerId = createOwner("Oliver");
        Long boatId = createBoat(ownerId, "Storm", "Moth");
        Long eventId = createEvent("Open Series");
        Long entryId = createEntry(boatId, eventId);
        Long driver1 = createDriver("Paula", "LIC-004");
        Long driver2 = createDriver("Quinn", "LIC-005");
        assignDriver(entryId, driver1, "HELMSMAN");
        assignDriver(entryId, driver2, "CREW");
        mockMvc.perform(delete("/entries/" + entryId + "/drivers/" + driver2))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeDriverFromEntry_unknownEntry_returns404() throws Exception {
        Long driverId = createDriver("Rosa", "LIC-R01");
        mockMvc.perform(delete("/entries/99999/drivers/" + driverId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(containsString("Entry not found")));
    }

    // ---- Input validation (400) ----

    @Test
    void createOwner_missingName_returns400() throws Exception {
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"contactEmail\":\"noname@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void registerBoat_missingOwnerId_returns400() throws Exception {
        mockMvc.perform(post("/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"No Owner\",\"boatClass\":\"Laser\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createEvent_missingDate_returns400() throws Exception {
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"No Date\",\"location\":\"Sydney\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createEntry_missingBoatId_returns400() throws Exception {
        mockMvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"eventId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createDriver_blankLicense_returns400() throws Exception {
        mockMvc.perform(post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"licenseNumber\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void listBoats_missingOwnerIdParam_returns400WithCustomBody() throws Exception {
        mockMvc.perform(get("/boats"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Required parameter 'ownerId' is missing"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void createOwner_malformedJson_returns400WithCustomBody() throws Exception {
        mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{bad json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Request body is missing or malformed"))
                .andExpect(jsonPath("$.timestamp").isString());
    }

    @Test
    void createEntry_unknownBoat_returns404() throws Exception {
        Long eventId = createEvent("Orphan Test");
        mockMvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"boatId\":99999,\"eventId\":" + eventId + "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void createEntry_unknownEvent_returns404() throws Exception {
        Long ownerId = createOwner("Vera");
        Long boatId = createBoat(ownerId, "Scout", "Laser");
        mockMvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"boatId\":" + boatId + ",\"eventId\":99999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void assignDriver_unknownDriver_returns404() throws Exception {
        Long ownerId = createOwner("Will");
        Long boatId = createBoat(ownerId, "Voyager", "Finn");
        Long eventId = createEvent("Open Race");
        Long entryId = createEntry(boatId, eventId);
        mockMvc.perform(post("/entries/" + entryId + "/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"driverId\":99999,\"role\":\"HELMSMAN\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    void updateEntryStatus_toCancelled_removesDrivers() throws Exception {
        Long ownerId = createOwner("Lena");
        Long boatId = createBoat(ownerId, "Tempest", "Laser");
        Long eventId = createEvent("Autumn Series");
        Long entryId = createEntry(boatId, eventId);
        Long driverId = createDriver("Marco", "LIC-M01");
        assignDriver(entryId, driverId, "HELMSMAN");

        mockMvc.perform(patch("/entries/" + entryId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/entries/" + entryId + "/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createEntry_orphanedBoat_returns409() throws Exception {
        Long ownerId = createOwner("Orphan");
        Long boatId = createBoat(ownerId, "Lost", "Laser");
        Long eventId = createEvent("Orphan Race");
        mockMvc.perform(delete("/owners/" + ownerId))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"boatId\":" + boatId + ",\"eventId\":" + eventId + "}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").isString());
    }

    // ---- Owner deletion cascade ----

    @Test
    void deleteOwner_cascadesOrphansBoatAndCancelsActiveEntry() throws Exception {
        Long ownerId = createOwner("Rachel");
        Long boatId = createBoat(ownerId, "Cascade", "Laser");
        Long eventId = createEvent("Cascade Test");
        Long entryId = createEntry(boatId, eventId);
        Long driverId = createDriver("Sam", "LIC-006");
        assignDriver(entryId, driverId, "HELMSMAN");
        mockMvc.perform(patch("/entries/" + entryId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/owners/" + ownerId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/entries?boatId=" + boatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entry.status").value("CANCELLED"));
    }

    // ---- Helpers ----

    private Long createOwner(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\",\"contactEmail\":\"" + name.toLowerCase() + "@test.com\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("ownerId").asLong();
    }

    private Long createBoat(Long ownerId, String name, String boatClass) throws Exception {
        MvcResult result = mockMvc.perform(post("/boats")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ownerId\":" + ownerId + ",\"name\":\"" + name + "\",\"boatClass\":\"" + boatClass + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("boatId").asLong();
    }

    private Long createEvent(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\",\"date\":\"2026-07-01\",\"location\":\"Harbour\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("eventId").asLong();
    }

    private Long createEntry(Long boatId, Long eventId) throws Exception {
        MvcResult result = mockMvc.perform(post("/entries")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"boatId\":" + boatId + ",\"eventId\":" + eventId + "}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("entryId").asLong();
    }

    private Long createDriver(String name, String license) throws Exception {
        MvcResult result = mockMvc.perform(post("/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\",\"licenseNumber\":\"" + license + "\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("driverId").asLong();
    }

    private void assignDriver(Long entryId, Long driverId, String role) throws Exception {
        mockMvc.perform(post("/entries/" + entryId + "/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"driverId\":" + driverId + ",\"role\":\"" + role + "\"}"))
                .andExpect(status().isCreated());
    }
}
