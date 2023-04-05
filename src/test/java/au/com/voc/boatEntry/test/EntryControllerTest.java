package au.com.voc.boatEntry.test;

import au.com.voc.raceEntry.RaceEntryApplication;
import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat_class.BoatClass;
import au.com.voc.raceEntry.config.WebSecurityConfig;
import au.com.voc.raceEntry.entry.EntryFormData;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.user.User;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {RaceEntryApplication.class}
)
/*@RunWith(SpringRunner.class)
@WithMockUser(username = "w",roles = "EMPLOYEE")

 */
@ContextConfiguration(classes = WebSecurityConfig.class)
@AutoConfigureMockMvc
public class EntryControllerTest {
    @LocalServerPort
    private int port;
    @MockBean
    private EntryService entryService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private MockMvc mockMvc;

    @Before()
    public void setup() {
        //Authentication authentication = Mockito.mock(Authentication.class);
        ////Mockito.when(securtiii)
        //mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @TestConfiguration
    static class DefaultConfigWithoutCsrf extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            super.configure(http);
            http.csrf().disable();
        }
    }

    @Test
    public void postLoginForm() throws Exception {
        Long eventId = Long.parseLong("2");
        EntryFormData entryFormData = new EntryFormData();
        entryFormData.setEntryId(-1);
        entryFormData.setEventId(eventId);
        User user = new User();
        List<Boat> boats = List.of(new Boat());
        List<Person> people = List.of(new Person());
        List<BoatClass> boatClasses = List.of(new BoatClass());
        entryFormData.setUser(user);
        mockMvc
                .perform(post("/login")
                        .param("username", "w").param("password", "1"))
                //.with(SecurityMockMvcRequestPostProcessors.user("w").password("1").roles("EMPLOYYEE")))
                .andExpect(status().isOk());

    }

    @Test
    public void loadForm() throws Exception {
        Long eventId = Long.parseLong("2");
        EntryFormData entryFormData = new EntryFormData();
        entryFormData.setEntryId(-1);
        entryFormData.setEventId(eventId);
        User user = new User();
        List<Boat> boats = List.of(new Boat());
        List<Person> people = List.of(new Person());
        List<BoatClass> boatClasses = List.of(new BoatClass());
        entryFormData.setUser(user);
        mockMvc
                .perform(get("http://localhost:" + port + "/entry/add/2")
                        .flashAttr("boats", boats)
                        .flashAttr("persons", people)
                        .flashAttr("boatClasses", boatClasses))
                //.with(SecurityMockMvcRequestPostProcessors.user("w").password("1").roles("EMPLOYYEE")))
                .andExpect(status().isOk());

    }
}
