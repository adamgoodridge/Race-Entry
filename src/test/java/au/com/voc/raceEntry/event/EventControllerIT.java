package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.security.JwtUtil;
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
class EventControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtUtil jwtUtil;
    @Autowired BoatClassRepository boatClassRepository;
    @Autowired AutoCloseScheduler autoCloseScheduler;

    private String adminToken;
    private String memberToken;

    @BeforeEach
    void setup() {
        adminToken = jwtUtil.generateToken("event_admin@test.com", "ADMIN");
        memberToken = jwtUtil.generateToken("event_member@test.com", "MEMBER");
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
}
