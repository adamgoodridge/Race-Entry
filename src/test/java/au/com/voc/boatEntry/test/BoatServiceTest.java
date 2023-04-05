package au.com.voc.boatEntry.test;

import au.com.voc.raceEntry.RaceEntryApplication;
import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.config.WebSecurityConfig;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RaceEntryApplication.class
)


@ContextConfiguration(classes = WebSecurityConfig.class)
public class BoatServiceTest {
    @Autowired
    private BoatService boatService;
    @Autowired
    private UserService userService;

    @Test
    @Transactional
    @WithMockUser(username = "w", authorities = {"EMPLOYEE"})
    public void validEventDateById() {
        User user = userService.getCurrentUser();
        java.util.List<Boat> boatList = boatService.getBoatsByUser(user);
        assertFalse(boatList.isEmpty());
    }
}
