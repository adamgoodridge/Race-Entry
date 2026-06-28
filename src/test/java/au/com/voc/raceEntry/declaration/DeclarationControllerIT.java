package au.com.voc.raceEntry.declaration;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.boatclass.BoatClass;
import au.com.voc.raceEntry.boatclass.BoatClassRepository;
import au.com.voc.raceEntry.entry.AddDriverRequest;
import au.com.voc.raceEntry.entry.EntryRequest;
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
class DeclarationControllerIT {

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
    private String adminToken;
    private Long memberPersonId;
    private Long boatId;
    private Long boatClassId;

    @BeforeEach
    void setup() {
        memberToken = jwtUtil.generateToken("dctl_member@test.com", "MEMBER");
        adminToken = jwtUtil.generateToken("dctl_admin@test.com", "ADMIN");

        User memberUser = userRepository.findByEmail("dctl_member@test.com")
                .orElseGet(() -> userRepository.save(
                        new User("Dctl", "Member", "dctl_member@test.com", "hash", Role.MEMBER)));

        Person memberPerson = personRepository.findByUserId(memberUser.getId())
                .orElseGet(() -> {
                    Person p = new Person();
                    p.setFirstName("Dctl");
                    p.setLastName("Member");
                    p.setPhone("0400000030");
                    p.setUserId(memberUser.getId());
                    return personRepository.save(p);
                });
        memberPersonId = memberPerson.getId();

        boatId = boatRepository.findAll().stream()
                .filter(b -> "DCT_BOAT".equals(b.getRaceNumber()))
                .findFirst()
                .map(Boat::getId)
                .orElseGet(() -> {
                    Boat b = new Boat();
                    b.setName("Decl Ctrl Boat");
                    b.setRaceNumber("DCT_BOAT");
                    b.setOwnerId(memberPersonId);
                    return boatRepository.save(b).getId();
                });

        boatClassId = boatClassRepository.findAll().stream()
                .filter(bc -> "DCT_CLASS".equals(bc.getName()))
                .findFirst()
                .map(BoatClass::getId)
                .orElseGet(() -> {
                    BoatClass bc = new BoatClass();
                    bc.setName("DCT_CLASS");
                    return boatClassRepository.save(bc).getId();
                });
    }

    private Long createEntryWithDriver(String eventName) throws Exception {
        EventRequest req = new EventRequest();
        req.setName(eventName);
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

        EntryRequest entryReq = new EntryRequest();
        entryReq.setBoatId(boatId);
        entryReq.setEventId(eventId);
        entryReq.setBoatClassId(boatClassId);
        MvcResult entryCreated = mockMvc.perform(post("/api/entries")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryReq)))
                .andExpect(status().isCreated())
                .andReturn();
        Long entryId = objectMapper.readTree(entryCreated.getResponse().getContentAsString()).get("id").asLong();

        Person driver = new Person();
        driver.setFirstName("Decl");
        driver.setLastName("Driver_" + eventName);
        driver.setPhone("0400000031");
        driver.setSbaLicenceNumber("SBA-001");
        driver.setSbaExpiryDate(LocalDate.of(2028, 1, 1));
        driver = personRepository.save(driver);

        AddDriverRequest addReq = new AddDriverRequest();
        addReq.setPersonId(driver.getId());
        mockMvc.perform(post("/api/entries/" + entryId + "/drivers")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isCreated());

        return entryId;
    }

    @Test
    void declaration_returns_pdf_for_owner() throws Exception {
        Long entryId = createEntryWithDriver("DCT_OwnerPdf");

        mockMvc.perform(get("/api/entries/" + entryId + "/declaration")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void declaration_returns_pdf_for_admin() throws Exception {
        Long entryId = createEntryWithDriver("DCT_AdminPdf");

        mockMvc.perform(get("/api/entries/" + entryId + "/declaration")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void declaration_as_non_owner_returns403() throws Exception {
        Long entryId = createEntryWithDriver("DCT_NonOwnerPdf");
        String strangerToken = jwtUtil.generateToken("dctl_stranger@test.com", "MEMBER");

        mockMvc.perform(get("/api/entries/" + entryId + "/declaration")
                .header("Authorization", "Bearer " + strangerToken))
                .andExpect(status().isForbidden());
    }
}
