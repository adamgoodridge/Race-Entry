package au.com.voc.raceEntry.controller;


import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.event.EventService;
import au.com.voc.raceEntry.event.EventFormData;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonService;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserAuthentication;
import au.com.voc.raceEntry.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class GeneralController {

    private final EventService eventService;
    private final PersonService personService;
    private final BoatService boatService;
    private final EntryService entryService;
    private final UserService userService;
    private final UserAuthentication userAuthentication;

    @Autowired
    public GeneralController(EventService eventService, PersonService personService, BoatService boatService, EntryService entryService, UserService userService, UserAuthentication userAuthentication) {
        this.eventService = eventService;
        this.personService = personService;
        this.boatService = boatService;
        this.entryService = entryService;
        this.userService = userService;
        this.userAuthentication = userAuthentication;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Person> drivers;
        if (userAuthentication.isAdmin()) {
            List<EventFormData> events = eventService.getEventsView(1);
            model.addAttribute("eventsView", events);
            return "admin-home";
        } else {
            User user = userService.getCurrentUser();
            drivers = personService.getPersonsByUser(user);
            model.addAttribute("persons", drivers);
            if (!drivers.isEmpty()) {
                List<Boat> boats = boatService.getBoatsByUser(user);
                model.addAttribute("boats", boats);
                if (!boats.isEmpty()) {
                    model.addAttribute("boatsLicenseExpired", boatService.byLicenseExpired(user.getId(), LocalDate.now()));
                    model.addAttribute("personsLicenseExpired", personService.getPersonsByLicenseExpired(user.getId()));
                    List<EventFormData> events = eventService.getEventsView(1);
                    model.addAttribute("eventsView", events);
                    List<Entry> entries = entryService.getEntriesByUser(user);
                    model.addAttribute("entries", entries);
                }
            }
            model.addAttribute("persons", drivers);
            return "home";
        }
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "login/login-form";
    }
}
