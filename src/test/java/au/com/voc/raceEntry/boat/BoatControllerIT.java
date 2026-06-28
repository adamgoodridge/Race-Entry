package au.com.voc.raceEntry.boat;

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
class BoatControllerIT {

    @MockBean JavaMailSender mailSender;

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtUtil jwtUtil;
    @Autowired UserRepository userRepository;
    @Autowired PersonRepository personRepository;

    private String memberToken;
    private String adminToken;
    private Long memberPersonId;

    @BeforeEach
    void setup() {
        User memberUser = userRepository.findByEmail("boat_member@test.com")
                .orElseGet(() -> userRepository.save(
                        new User("Boat", "Member", "boat_member@test.com", "hash", Role.MEMBER)));
        Person memberPerson = personRepository.findByUserId(memberUser.getId())
                .orElseGet(() -> {
                    Person p = new Person();
                    p.setFirstName("Boat");
                    p.setLastName("Member");
                    p.setPhone("0400000001");
                    p.setUserId(memberUser.getId());
                    return personRepository.save(p);
                });
        memberPersonId = memberPerson.getId();

        memberToken = jwtUtil.generateToken("boat_member@test.com", "MEMBER");
        adminToken = jwtUtil.generateToken("boat_admin@test.com", "ADMIN");
    }

    private String boatJson(String name, String raceNumber) throws Exception {
        BoatRequest req = new BoatRequest();
        req.setName(name);
        req.setRaceNumber(raceNumber);
        return objectMapper.writeValueAsString(req);
    }

    @Test
    void create_boat_returns201_with_caller_as_owner() throws Exception {
        mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("Speedy", "B001")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Speedy"))
                .andExpect(jsonPath("$.raceNumber").value("B001"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.ownerId").value(memberPersonId));
    }

    @Test
    void create_boat_duplicate_race_number_returns409() throws Exception {
        mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("First", "DUP01")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("Second", "DUP01")))
                .andExpect(status().isConflict());
    }

    @Test
    void list_boats_as_admin_returns_all() throws Exception {
        mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("AdminListBoat", "AL01")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/boats")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.raceNumber == 'AL01')]").exists());
    }

    @Test
    void list_boats_as_member_returns_own_boats_only() throws Exception {
        mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("MemberBoat", "MB01")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/boats")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.raceNumber == 'MB01')]").exists())
                .andExpect(jsonPath("$[?(@.ownerId != " + memberPersonId + ")]").doesNotExist());
    }

    @Test
    void get_boat_by_id_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("GetBoat", "GB01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/boats/" + id)
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("GetBoat"));
    }

    @Test
    void get_boat_by_id_not_found_returns404() throws Exception {
        mockMvc.perform(get("/api/boats/999999")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_boat_as_owner_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("OldName", "UP01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        BoatRequest update = new BoatRequest();
        update.setName("NewName");

        mockMvc.perform(put("/api/boats/" + id)
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.raceNumber").value("UP01"));
    }

    @Test
    void update_boat_as_non_owner_returns403() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("OtherBoat", "OB01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        String otherMemberToken = jwtUtil.generateToken("other_member@test.com", "MEMBER");
        BoatRequest update = new BoatRequest();
        update.setName("Hijacked");

        mockMvc.perform(put("/api/boats/" + id)
                .header("Authorization", "Bearer " + otherMemberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deactivate_boat_as_owner_returns200_with_inactive_status() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("DeactBoat", "DA01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/boats/" + id + "/deactivate")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void assign_owner_as_admin_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("AssignOwnerBoat", "AO01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        Person newOwner = new Person();
        newOwner.setFirstName("New");
        newOwner.setLastName("Owner");
        newOwner.setPhone("0400000002");
        newOwner = personRepository.save(newOwner);
        Long newOwnerId = newOwner.getId();

        mockMvc.perform(put("/api/boats/" + id + "/owner/" + newOwnerId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").value(newOwnerId));
    }

    @Test
    void assign_owner_as_member_returns403() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("AssignOwnerMemberBoat", "AOM01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/boats/" + id + "/owner/999")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void remove_owner_as_admin_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("RemoveOwnerBoat", "RO01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/boats/" + id + "/owner")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerId").doesNotExist());
    }

    @Test
    void remove_owner_as_member_returns403() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boats")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(boatJson("RemoveOwnerMemberBoat", "ROM01")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/boats/" + id + "/owner")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isForbidden());
    }
}
