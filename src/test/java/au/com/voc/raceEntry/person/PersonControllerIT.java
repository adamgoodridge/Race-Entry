package au.com.voc.raceEntry.person;

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
class PersonControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    private String memberToken;
    private String adminToken;

    @BeforeEach
    void setup() {
        memberToken = jwtUtil.generateToken("person_member@test.com", "MEMBER");
        adminToken = jwtUtil.generateToken("person_admin@test.com", "ADMIN");
    }

    private PersonRequest buildRequest(String firstName, String lastName) {
        PersonRequest req = new PersonRequest();
        req.setFirstName(firstName);
        req.setLastName(lastName);
        req.setPhone("0400000000");
        return req;
    }

    @Test
    void create_person_returns201() throws Exception {
        mockMvc.perform(post("/api/persons")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("Alice", "Smith"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void list_persons_as_admin_returns200() throws Exception {
        mockMvc.perform(post("/api/persons")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("Bob", "Jones"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/persons")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void list_persons_as_member_returns403() throws Exception {
        mockMvc.perform(get("/api/persons")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void get_person_by_id_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/persons")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("Carol", "White"))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/persons/" + id)
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Carol"));
    }

    @Test
    void get_person_by_id_not_found_returns404() throws Exception {
        mockMvc.perform(get("/api/persons/999999")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_person_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/persons")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("Dave", "Brown"))))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        PersonRequest update = new PersonRequest();
        update.setFirstName("David");
        update.setPhone("0411111111");

        mockMvc.perform(put("/api/persons/" + id)
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("David"))
                .andExpect(jsonPath("$.phone").value("0411111111"))
                .andExpect(jsonPath("$.lastName").value("Brown"));
    }

    @Test
    void update_person_not_found_returns404() throws Exception {
        mockMvc.perform(put("/api/persons/999999")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("X", "Y"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void reassign_user_as_admin_returns200() throws Exception {
        User user = new User("Eve", "Taylor", "person_eve@test.com", "hash", Role.MEMBER);
        user = userRepository.save(user);
        Long userId = user.getId();

        MvcResult created = mockMvc.perform(post("/api/persons")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("Eve", "Taylor"))))
                .andExpect(status().isCreated())
                .andReturn();

        Long personId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/persons/" + personId + "/user/" + userId)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void reassign_user_as_member_returns403() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/persons")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildRequest("Frank", "Green"))))
                .andExpect(status().isCreated())
                .andReturn();

        Long personId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(put("/api/persons/" + personId + "/user/1")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isForbidden());
    }
}
