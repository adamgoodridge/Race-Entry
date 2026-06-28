package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventBoatClass;
import au.com.voc.raceEntry.event.EventBoatClassRepository;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.event.EventRequest;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.security.JwtUtil;
import au.com.voc.raceEntry.user.Role;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

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
    "server.ssl.enabled=false",
    "spring.mail.host=localhost",
    "connection.driver_class=",
    "connection.pool.initialPoolSize=",
    "connection.pool.minPoolSize=",
    "connection.pool.maxPoolSize=",
    "connection.pool.maxIdleTime="
})
class EntryControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtUtil jwtUtil;
    @Autowired UserRepository userRepository;
    @Autowired PersonRepository personRepository;
    @Autowired BoatRepository boatRepository;
    @Autowired BoatClassRepository boatClassRepository;
    @Autowired EventRepository eventRepository;
    @Autowired EventBoatClassRepository eventBoatClassRepository;
    @Autowired EntryDriverRepository entryDriverRepository;

    private String memberToken;
    private String adminToken;
    private Long memberPersonId;
    private Long boatId;
    private Long boatClassId;

    @BeforeEach
    void setup() {
        memberToken = jwtUtil.generateToken("ectl_member@test.com", "MEMBER");
        adminToken = jwtUtil.generateToken("ectl_admin@test.com", "ADMIN");

        User memberUser = userRepository.findByEmail("ectl_member@test.com")
                .orElseGet(() -> userRepository.save(
                        new User("Ectl", "Member", "ectl_member@test.com", "hash", Role.MEMBER)));

        Person memberPerson = personRepository.findByUserId(memberUser.getId())
                .orElseGet(() -> {
                    Person p = new Person();
                    p.setFirstName("Ectl");
                    p.setLastName("Member");
                    p.setPhone("0400000020");
                    p.setUserId(memberUser.getId());
                    return personRepository.save(p);
                });
        memberPersonId = memberPerson.getId();

        boatId = boatRepository.findAll().stream()
                .filter(b -> "ECT_BOAT".equals(b.getRaceNumber()))
                .findFirst()
                .map(Boat::getId)
                .orElseGet(() -> {
                    Boat b = new Boat();
                    b.setName("Entry Ctrl Boat");
                    b.setRaceNumber("ECT_BOAT");
                    b.setOwnerId(memberPersonId);
                    return boatRepository.save(b).getId();
                });

        boatClassId = boatClassRepository.findAll().stream()
                .filter(bc -> "ECT_CLASS".equals(bc.getName()))
                .findFirst()
                .map(BoatClass::getId)
                .orElseGet(() -> {
                    BoatClass bc = new BoatClass();
                    bc.setName("ECT_CLASS");
                    return boatClassRepository.save(bc).getId();
                });
    }

    private Long createOpenEventWithBoatClass(String name) throws Exception {
        EventRequest req = new EventRequest();
        req.setName(name);
        req.setStartDate(LocalDate.of(2027, 6, 1));
        req.setClosingDeadline(LocalDate.of(2027, 5, 31));
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        if (!eventBoatClassRepository.existsByEventIdAndBoatClassId(eventId, boatClassId)) {
            mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + boatClassId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
        }
        return eventId;
    }

    private String entryJson(Long boatId, Long eventId, Long boatClassId) throws Exception {
        EntryRequest req = new EntryRequest();
        req.setBoatId(boatId);
        req.setEventId(eventId);
        req.setBoatClassId(boatClassId);
        return objectMapper.writeValueAsString(req);
    }

    @Test
    void create_entry_as_owner_returns201() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_CreateOwner");

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.boatId").value(boatId))
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void create_entry_as_non_owner_returns403() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_CreateNonOwner");
        String otherToken = jwtUtil.generateToken("ectl_other@test.com", "MEMBER");

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_entry_duplicate_boat_event_returns409() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_DuplicateEntry");

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isConflict());
    }

    @Test
    void create_entry_event_not_open_returns422() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_ClosedEvent");

        mockMvc.perform(post("/api/events/" + eventId + "/close")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void create_entry_boatclass_not_in_event_returns422() throws Exception {
        EventRequest req = new EventRequest();
        req.setName("ECT_NoBoatClass");
        req.setStartDate(LocalDate.of(2027, 6, 1));
        req.setClosingDeadline(LocalDate.of(2027, 5, 31));
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void list_entries_as_admin_returns_all() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_ListAdmin");

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/entries")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void list_entries_as_member_returns_own_only() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_ListMember");

        mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/entries")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.boatId != " + boatId + ")]").doesNotExist());
    }

    @Test
    void get_entry_by_id_returns200() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_GetById");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/entries/" + entryId)
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entryId))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void get_entry_not_found_returns404() throws Exception {
        mockMvc.perform(get("/api/entries/999999")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void submit_entry_happy_path_returns200() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_SubmitHappy");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        // Add a driver with a valid SBA
        Person driver = new Person();
        driver.setFirstName("Valid");
        driver.setLastName("Driver");
        driver.setPhone("0400000021");
        driver.setSbaExpiryDate(LocalDate.of(2028, 1, 1)); // expires after event startDate 2027-06-01
        driver = personRepository.save(driver);
        Long driverPersonId = driver.getId();

        AddDriverRequest addReq = new AddDriverRequest();
        addReq.setPersonId(driverPersonId);
        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/entries/" + entryId + "/submit")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void submit_entry_event_not_open_returns422() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_SubmitClosed");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/events/" + eventId + "/close")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/entries/" + entryId + "/submit")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void submit_entry_no_owner_returns422() throws Exception {
        // Create a boat with no owner (admin creates entry for it)
        Boat noOwnerBoat = new Boat();
        noOwnerBoat.setName("No Owner Boat");
        noOwnerBoat.setRaceNumber("ECT_NOOWNER");
        noOwnerBoat = boatRepository.save(noOwnerBoat);
        Long noOwnerBoatId = noOwnerBoat.getId();

        Long eventId = createOpenEventWithBoatClass("ECT_SubmitNoOwner");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(noOwnerBoatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/entries/" + entryId + "/submit")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void submit_entry_no_drivers_returns422() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_SubmitNoDrivers");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/entries/" + entryId + "/submit")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void submit_entry_expired_sba_returns422() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_SubmitExpiredSba");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        // Add driver with expired SBA (before event startDate 2027-06-01)
        Person expiredDriver = new Person();
        expiredDriver.setFirstName("Expired");
        expiredDriver.setLastName("Driver");
        expiredDriver.setPhone("0400000022");
        expiredDriver.setSbaExpiryDate(LocalDate.of(2026, 1, 1));
        expiredDriver = personRepository.save(expiredDriver);
        Long expiredDriverId = expiredDriver.getId();

        AddDriverRequest addReq = new AddDriverRequest();
        addReq.setPersonId(expiredDriverId);
        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/entries/" + entryId + "/submit")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void cancel_entry_as_owner_returns200() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_CancelOwner");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/entries/" + entryId + "/cancel")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancel_entry_as_non_owner_returns403() throws Exception {
        Long eventId = createOpenEventWithBoatClass("ECT_CancelNonOwner");

        MvcResult created = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entryJson(boatId, eventId, boatClassId)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        String otherToken = jwtUtil.generateToken("ectl_stranger@test.com", "MEMBER");
        mockMvc.perform(post("/api/entries/" + entryId + "/cancel")
                .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isForbidden());
    }
}
