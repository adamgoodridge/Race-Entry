package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.event.EventBoatClass;
import au.com.voc.raceEntry.event.EventBoatClassRepository;
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
class EntryDriverControllerIT {

    @MockBean JavaMailSender mailSender;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtUtil jwtUtil;
    @Autowired UserRepository userRepository;
    @Autowired PersonRepository personRepository;
    @Autowired BoatRepository boatRepository;
    @Autowired BoatClassRepository boatClassRepository;
    @Autowired EventBoatClassRepository eventBoatClassRepository;

    private String memberToken;
    private Long memberPersonId;
    private Long boatId;
    private Long boatClassId;

    @BeforeEach
    void setup() {
        memberToken = jwtUtil.generateToken("edctl_member@test.com", "MEMBER");

        User memberUser = userRepository.findByEmail("edctl_member@test.com")
                .orElseGet(() -> userRepository.save(
                        new User("Edctl", "Member", "edctl_member@test.com", "hash", Role.MEMBER)));

        Person memberPerson = personRepository.findByUserId(memberUser.getId())
                .orElseGet(() -> {
                    Person p = new Person();
                    p.setFirstName("Edctl");
                    p.setLastName("Member");
                    p.setPhone("0400000030");
                    p.setUserId(memberUser.getId());
                    return personRepository.save(p);
                });
        memberPersonId = memberPerson.getId();

        boatId = boatRepository.findAll().stream()
                .filter(b -> "EDT_BOAT".equals(b.getRaceNumber()))
                .findFirst()
                .map(Boat::getId)
                .orElseGet(() -> {
                    Boat b = new Boat();
                    b.setName("Driver Test Boat");
                    b.setRaceNumber("EDT_BOAT");
                    b.setOwnerId(memberPersonId);
                    return boatRepository.save(b).getId();
                });

        boatClassId = boatClassRepository.findAll().stream()
                .filter(bc -> "EDT_CLASS".equals(bc.getName()))
                .findFirst()
                .map(BoatClass::getId)
                .orElseGet(() -> {
                    BoatClass bc = new BoatClass();
                    bc.setName("EDT_CLASS");
                    return boatClassRepository.save(bc).getId();
                });
    }

    private Long createEntry(String eventName) throws Exception {
        String adminToken = jwtUtil.generateToken("edctl_admin@test.com", "ADMIN");

        EventRequest req = new EventRequest();
        req.setName(eventName);
        req.setStartDate(LocalDate.of(2027, 8, 1));
        req.setClosingDeadline(LocalDate.of(2027, 7, 31));
        MvcResult evtResult = mockMvc.perform(post("/api/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();
        Long eventId = objectMapper.readTree(evtResult.getResponse().getContentAsString()).get("id").asLong();

        if (!eventBoatClassRepository.existsByEventIdAndBoatClassId(eventId, boatClassId)) {
            mockMvc.perform(post("/api/events/" + eventId + "/boat-classes/" + boatClassId)
                    .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk());
        }

        EntryRequest entryReq = new EntryRequest();
        entryReq.setBoatId(boatId);
        entryReq.setEventId(eventId);
        entryReq.setBoatClassId(boatClassId);
        MvcResult entryResult = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryReq)))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(entryResult.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long createPerson(String firstName, String lastName) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setPhone("0400000031");
        return personRepository.save(p).getId();
    }

    private String addDriverJson(Long personId) throws Exception {
        AddDriverRequest req = new AddDriverRequest();
        req.setPersonId(personId);
        return objectMapper.writeValueAsString(req);
    }

    @Test
    void add_driver_as_owner_returns201() throws Exception {
        Long entryId = createEntry("EDT_AddDriver");
        Long personId = createPerson("Add", "Driver");

        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDriverJson(personId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.entryId").value(entryId))
                .andExpect(jsonPath("$.personId").value(personId));
    }

    @Test
    void add_driver_duplicate_returns409() throws Exception {
        Long entryId = createEntry("EDT_DupDriver");
        Long personId = createPerson("Dup", "Driver");

        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDriverJson(personId)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDriverJson(personId)))
                .andExpect(status().isConflict());
    }

    @Test
    void add_driver_as_non_owner_returns403() throws Exception {
        Long entryId = createEntry("EDT_NonOwner");
        Long personId = createPerson("NonOwner", "Driver");
        String otherToken = jwtUtil.generateToken("edctl_stranger@test.com", "MEMBER");

        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDriverJson(personId)))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_drivers_returns200() throws Exception {
        Long entryId = createEntry("EDT_ListDrivers");
        Long personId = createPerson("List", "Driver");

        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDriverJson(personId)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].personId").value(personId));
    }

    @Test
    void remove_driver_as_owner_returns204() throws Exception {
        Long entryId = createEntry("EDT_RemoveDriver");
        Long personId = createPerson("Remove", "Driver");

        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(addDriverJson(personId)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/entries/" + entryId + "/drivers/" + personId)
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void remove_driver_not_found_returns404() throws Exception {
        Long entryId = createEntry("EDT_RemoveNotFound");

        mockMvc.perform(delete("/api/entries/" + entryId + "/drivers/999999")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNotFound());
    }
}
