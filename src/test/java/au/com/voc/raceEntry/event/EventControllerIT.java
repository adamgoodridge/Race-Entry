package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryRepository;
import au.com.voc.raceEntry.entry.EntryRequest;
import au.com.voc.raceEntry.entry.EntryStatus;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
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
class EventControllerIT {

    @MockBean JavaMailSender mailSender;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtUtil jwtUtil;
    @Autowired BoatClassRepository boatClassRepository;
    @Autowired BoatRepository boatRepository;
    @Autowired PersonRepository personRepository;
    @Autowired UserRepository userRepository;
    @Autowired EntryRepository entryRepository;
    @Autowired EventBoatClassRepository eventBoatClassRepository;
    @Autowired AutoCloseScheduler autoCloseScheduler;

    private String adminToken;
    private String memberToken;
    private Long cascadeBoatId;
    private Long cascadePersonId;

    @BeforeEach
    void setup() {
        adminToken = jwtUtil.generateToken("event_admin@test.com", "ADMIN");
        memberToken = jwtUtil.generateToken("event_member@test.com", "MEMBER");

        User memberUser = userRepository.findByEmail("evcit_member@test.com")
                .orElseGet(() -> userRepository.save(
                        new User("Evcit", "Member", "evcit_member@test.com", "hash", Role.MEMBER)));

        Person memberPerson = personRepository.findByUserId(memberUser.getId())
                .orElseGet(() -> {
                    Person p = new Person();
                    p.setFirstName("Evcit");
                    p.setLastName("Member");
                    p.setPhone("0400000030");
                    p.setUserId(memberUser.getId());
                    return personRepository.save(p);
                });
        cascadePersonId = memberPerson.getId();

        cascadeBoatId = boatRepository.findAll().stream()
                .filter(b -> "EVCIT_BOAT".equals(b.getRaceNumber()))
                .findFirst()
                .map(Boat::getId)
                .orElseGet(() -> {
                    Boat b = new Boat();
                    b.setName("Event Ctrl IT Boat");
                    b.setRaceNumber("EVCIT_BOAT");
                    b.setOwnerId(cascadePersonId);
                    return boatRepository.save(b).getId();
                });
    }

    private String eventJson(String name, LocalDate startDate, LocalDate closingDeadline) throws Exception {
        EventRequest req = new EventRequest();
        req.setName(name);
        req.setStartDate(startDate);
        req.setClosingDeadline(closingDeadline);
        return objectMapper.writeValueAsString(req);
    }

    @Test
    void create_event_as_admin_returns201() throws Exception {
        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Summer Race", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Summer Race"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void create_event_as_member_returns403() throws Exception {
        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Member Event", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_events_returns200() throws Exception {
        mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("List Test Event", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/events")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void get_event_by_id_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Get Event", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/events/" + id)
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Get Event"));
    }

    @Test
    void get_event_not_found_returns404() throws Exception {
        mockMvc.perform(get("/api/events/999999")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_event_as_admin_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Old Name", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        EventRequest update = new EventRequest();
        update.setName("New Name");

        mockMvc.perform(put("/api/events/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void update_event_as_member_returns403() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Member Update Event", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        EventRequest update = new EventRequest();
        update.setName("Hijacked");

        mockMvc.perform(put("/api/events/" + id)
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    void close_event_as_admin_returns200_with_closed_status() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Close Me", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/events/" + id + "/close")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void cancel_event_as_admin_returns200_with_cancelled_status() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Cancel Me", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/events/" + id + "/cancel")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void add_boat_class_to_event_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Class Event", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        BoatClass bc = new BoatClass();
        bc.setName("EBC_TestClass_Add");
        bc = boatClassRepository.save(bc);
        Long classId = bc.getId();

        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.boatClassId").value(classId));
    }

    @Test
    void add_duplicate_boat_class_returns409() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Dup Class Event", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        BoatClass bc = new BoatClass();
        bc.setName("EBC_TestClass_Dup");
        bc = boatClassRepository.save(bc);
        Long classId = bc.getId();

        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());
    }

    @Test
    void remove_boat_class_returns200_and_can_readd() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("Remove Class Event", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 25))))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        BoatClass bc = new BoatClass();
        bc.setName("EBC_TestClass_Remove");
        bc = boatClassRepository.save(bc);
        Long classId = bc.getId();

        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/events/" + eventId + "/boat-classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Can re-add after removal
        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void auto_close_scheduler_closes_events_past_deadline() throws Exception {
        EventRequest req = new EventRequest();
        req.setName("Expired Event");
        req.setStartDate(LocalDate.of(2026, 5, 1));
        req.setClosingDeadline(LocalDate.of(2026, 5, 31)); // well in the past

        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        // Trigger the scheduler directly
        autoCloseScheduler.closeExpiredEvents();

        mockMvc.perform(get("/api/events/" + id)
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    void cancel_event_cascades_to_cancel_all_entries() throws Exception {
        BoatClass bc = new BoatClass();
        bc.setName("EVCIT_CancelCascade_Class");
        bc = boatClassRepository.save(bc);
        Long classId = bc.getId();

        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("EVCIT_CancelCascade", LocalDate.of(2027, 10, 1), LocalDate.of(2027, 9, 30))))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        EntryRequest entryReq = new EntryRequest();
        entryReq.setBoatId(cascadeBoatId);
        entryReq.setEventId(eventId);
        entryReq.setBoatClassId(classId);
        MvcResult entryCreated = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryReq)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(entryCreated.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/events/" + eventId + "/cancel")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        Entry entry = entryRepository.findById(entryId).orElseThrow();
        assert entry.getStatus() == EntryStatus.CANCELLED;
    }

    @Test
    void remove_boat_class_cascades_to_cancel_entries_in_that_class() throws Exception {
        BoatClass bc1 = new BoatClass();
        bc1.setName("EVCIT_RemoveCascade_C1");
        bc1 = boatClassRepository.save(bc1);
        Long classId1 = bc1.getId();

        BoatClass bc2 = new BoatClass();
        bc2.setName("EVCIT_RemoveCascade_C2");
        bc2 = boatClassRepository.save(bc2);
        Long classId2 = bc2.getId();

        Boat boat2 = new Boat();
        boat2.setName("EVCIT Boat2");
        boat2.setRaceNumber("EVCIT_BOAT2");
        boat2.setOwnerId(cascadePersonId);
        boat2 = boatRepository.save(boat2);
        Long boat2Id = boat2.getId();

        MvcResult created = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(eventJson("EVCIT_RemoveCascade", LocalDate.of(2027, 10, 1), LocalDate.of(2027, 9, 30))))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId1)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + classId2)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // Entry in class1 (will be cancelled)
        EntryRequest req1 = new EntryRequest();
        req1.setBoatId(cascadeBoatId);
        req1.setEventId(eventId);
        req1.setBoatClassId(classId1);
        MvcResult e1 = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req1)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId1 = objectMapper.readTree(e1.getResponse().getContentAsString()).get("id").asLong();

        // Entry in class2 (must remain DRAFT)
        EntryRequest req2 = new EntryRequest();
        req2.setBoatId(boat2Id);
        req2.setEventId(eventId);
        req2.setBoatClassId(classId2);
        MvcResult e2 = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req2)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId2 = objectMapper.readTree(e2.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/events/" + eventId + "/boat-classes/" + classId1)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        Entry entry1 = entryRepository.findById(entryId1).orElseThrow();
        Entry entry2 = entryRepository.findById(entryId2).orElseThrow();
        assert entry1.getStatus() == EntryStatus.CANCELLED;
        assert entry2.getStatus() == EntryStatus.DRAFT;
    }
}
