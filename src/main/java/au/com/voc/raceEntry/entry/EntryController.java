//demo only
package au.com.voc.raceEntry.entry;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.boat_class.BoatClass;
import au.com.voc.raceEntry.boat_class.BoatClassService;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventService;
import au.com.voc.raceEntry.event.EventView;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonService;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserService;
import au.com.voc.raceEntry.utils.CheckExpiryValidator;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/entry")
public class EntryController {
    private final EntryService entryService;
    private final BoatClassService boatClassService;
    private final PersonService personService;
    private final BoatService boatService;
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public EntryController(EntryService entryService, BoatClassService boatClassService, PersonService personService, BoatService boatService, EventService eventService, UserService userService) {
        this.entryService = entryService;
        this.boatClassService = boatClassService;
        this.personService = personService;
        this.boatService = boatService;
        this.eventService = eventService;
        this.userService = userService;
    }


    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @RequestMapping("/add/{eventId}")
    private String form(@PathVariable("eventId") long eventId, Model model, HttpServletRequest request) {
        User user = userService.getCurrentUser();

        EntryFormData entryFormData = new EntryFormData();
        entryFormData.setEntryId(-1);
        entryFormData.setEventId(eventId);
        entryFormData.setUser(user);

        String referer = request.getHeader("Referer");
        entryFormData.setPreviousUrl(referer);

        loadFormData(model);


        model.addAttribute("entryFormData", entryFormData);
        Event event = eventService.getEvent(eventId);
        event.setDescription(StringEscapeUtils.escapeHtml4(event.getDescription()));
        model.addAttribute("event", event);
        return "modelEntry/add-entry-form";
    }

    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("entryFormData") EntryFormData entryFormData, BindingResult bindingResult, Model model) {
        Entry entry = entryService.formEventEntry(entryFormData);
        CheckExpiryValidator checkExpiryValidator = new CheckExpiryValidator(entry);

        if (bindingResult.hasErrors() || checkExpiryValidator.isValid()) {
            loadFormData(model);
            Event event = eventService.getEvent(entryFormData.getEventId());
            event.setDescription(StringEscapeUtils.escapeHtml4(event.getDescription()));
            model.addAttribute("event", event);
            model.addAttribute("entryForData", entryFormData);
            if (checkExpiryValidator.isValid()) {
                model.addAttribute("expiredErrors", checkExpiryValidator.getMessage());
            }
            return "modelEntry/add-entry-form";
        } else {
            entryService.updateEntry(entry);
            return "redirect:" + entryFormData.getPreviousUrl();
        }
    }

    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model, HttpServletRequest request) {
        Entry entry = entryService.getEntry(id);
        User user = userService.getCurrentUser();
        if (entry.getUser().equals(user) || user.isAdmin()) {
            EntryFormData entryFormData = new EntryFormData(entry);
            loadFormData(model);
            model.addAttribute("entryFormData", entryFormData);
            Event event = eventService.getEvent(entryFormData.getEventId());
            model.addAttribute("event", event);
            String referer = request.getHeader("Referer");
            entryFormData.setPreviousUrl(referer);
            return "modelEntry/add-entry-form";
        } else {
            return "access-denied";
        }
    }

    @RequestMapping("/entriesByEvent/{eventId}")
    public String entriesByEvent(@PathVariable(value = "eventId") long eventId, Model model) {
        List<Entry> entries = entryService.getEntriesByEvent(eventId);
        Map<String, List<Entry>> entryBoatClassGrouped = entries.stream().collect(Collectors.groupingBy(Entry::getBoatClass));
        model.addAttribute("eventId", eventId);
        model.addAttribute("entries", entries);
        model.addAttribute("entryBoatClassGrouped", entryBoatClassGrouped);
        return "modelEntry/entries-by-event";
    }

    //https://stackoverflow.com/questions/5673260/downloading-a-file-from-spring-controllers
    @RequestMapping("/printForm/{id}")
    //bad practice return object
    private ResponseEntity<?> downloadForm(@PathVariable("id") Long id) throws IOException {
        ResponseEntity<?> response;
        Entry entry = entryService.getEntry(id);
        User user = userService.getCurrentUser();
        if (entry.getUser().getId().equals(user.getId()) || user.isAdmin()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // here you have to set the actual filename of your pdf
            String filename = entry.getEvent().getName() + " " + entry.getBoat().getName() + " Entry Form.pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            byte[] contents = entryService.generatePDF(entry);
            response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("Access is denied", new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping("/start")
    public String start(Model model) {
        List<EventView> events = eventService.getEventsView(1);
        model.addAttribute("eventsView", events);
        User user = userService.getCurrentUser();
        if (user.isAdmin()) {
            return "modelEvent/list-events";
        } else {
            List<Entry> entries = entryService.getEntriesByUser(user);
            model.addAttribute("entries", entries);
            return "modelEvent/list-opened-events-driver";
        }
    }

    private void loadFormData(Model model) {
        User user = userService.getCurrentUser();
        List<Boat> boats;
        List<Person> drivers;
        if (user.isAdmin()) {
            boats = boatService.getAllBoats();
            drivers = personService.getAllPersons();
        } else {
            boats = boatService.getBoatsByUser(user);
            drivers = personService.getPersonsByUser(user);
        }
        List<BoatClass> boatClasses = boatClassService.getBoatClasses();
        model.addAttribute("boats", boats);
        model.addAttribute("persons", drivers);
        model.addAttribute("boatClasses", boatClasses);
    }
}
