package au.com.voc.raceEntry.boatclass;

import au.com.voc.raceEntry.security.JwtUtil;
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

import static org.hamcrest.Matchers.hasSize;
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
class BoatClassControllerIT {

    @MockBean JavaMailSender mailSender;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtUtil jwtUtil;

    private String memberToken;
    private String adminToken;

    @BeforeEach
    void setup() {
        memberToken = jwtUtil.generateToken("bc_member@test.com", "MEMBER");
        adminToken = jwtUtil.generateToken("bc_admin@test.com", "ADMIN");
    }

    private String json(String name) throws Exception {
        BoatClassRequest req = new BoatClassRequest();
        req.setName(name);
        return objectMapper.writeValueAsString(req);
    }

    @Test
    void create_as_admin_returns201() throws Exception {
        mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("Formula 500")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Formula 500"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void create_as_member_returns403() throws Exception {
        mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + memberToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("Open")))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_as_admin_returns_all_including_inactive() throws Exception {
        mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("AdminList_Active")))
                .andExpect(status().isCreated());

        MvcResult r2 = mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("AdminList_Inactive")))
                .andExpect(status().isCreated())
                .andReturn();

        Long inactiveId = objectMapper.readTree(r2.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(patch("/api/boat-classes/" + inactiveId + "/deactivate")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'AdminList_Active')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'AdminList_Inactive')]").exists());
    }

    @Test
    void list_as_member_returns_active_only() throws Exception {
        mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("MemberList_Active")))
                .andExpect(status().isCreated());

        MvcResult r2 = mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("MemberList_Inactive")))
                .andExpect(status().isCreated())
                .andReturn();

        Long inactiveId = objectMapper.readTree(r2.getResponse().getContentAsString()).get("id").asLong();
        mockMvc.perform(patch("/api/boat-classes/" + inactiveId + "/deactivate")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/boat-classes")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'MemberList_Active')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'MemberList_Inactive')]", hasSize(0)));
    }

    @Test
    void get_by_id_returns200() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("GetById_Class")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/boat-classes/" + id)
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("GetById_Class"));
    }

    @Test
    void get_by_id_not_found_returns404() throws Exception {
        mockMvc.perform(get("/api/boat-classes/999999")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivate_as_admin_returns200_with_inactive_status() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("Deactivate_Class")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/boat-classes/" + id + "/deactivate")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void deactivate_as_member_returns403() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/boat-classes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json("MemberDeact_Class")))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(patch("/api/boat-classes/" + id + "/deactivate")
                .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isForbidden());
    }
}
