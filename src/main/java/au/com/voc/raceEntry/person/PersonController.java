//demo only
package au.com.voc.raceEntry.person;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatService;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserAuthentication;
import au.com.voc.raceEntry.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/person")
public class PersonController {

    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    private final PersonService personService;
    private final BoatService boatService;
    private final EntryService entryService;
    private final UserService userService;
    private final UserAuthentication userAuthentication;

    @Autowired
    public PersonController(PersonService personService, BoatService boatService, EntryService entryService, UserService userService, UserAuthentication userAuthentication) {
        this.personService = personService;
        this.boatService = boatService;
        this.entryService = entryService;
        this.userService = userService;
        this.userAuthentication = userAuthentication;
    }


    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @RequestMapping("/add")
    public String showFormPerson(Model model, HttpServletRequest request) {
        Person person = new Person();
        person.setPersonId(Long.parseLong("-1"));
        person.setDeleted(false);
        request.getSession().setAttribute("personFormReturnUrl", request.getHeader("Referer"));
        User user = userService.getCurrentUser();
        person.setUserId(user.getId());
        model.addAttribute("person", person);
        return "modelPerson/add-person-form";
    }

    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("person") Person person, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
            return "modelPerson/add-person-form";
        } else {
            personService.updatePerson(person);
            String returnUrl = (String) request.getSession().getAttribute("personFormReturnUrl");
            return "redirect:" + (returnUrl != null ? returnUrl : "/");
        }
    }

    @RequestMapping("/list")
    public String listPersons(Model model) {
        User user = userService.getCurrentUser();
        List<Person> persons;
        if (user.isAdmin()) {
            persons = personService.getAllPersons();
        } else {
            persons = personService.getPersonsByUser(user);
        }
        model.addAttribute("persons", persons);
        return "modelPerson/list-drivers";
    }

    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model, HttpServletRequest request) {
        Person driver = personService.getPerson(id);
        User user = userService.getCurrentUser();
        if (user.isAdmin() || driver.getUser().getId().equals(user.getId())) {
            request.getSession().setAttribute("personFormReturnUrl", request.getHeader("Referer"));
            model.addAttribute("person", driver);
            return "modelPerson/add-person-form";
        } else {
            return "access-denied";
        }
    }

    @RequestMapping("/delete/{id}")
    public String deletePerson(@PathVariable("id") long id, Model model) {
        Person driver = personService.getPerson(id);
        List<Boat> boatsByOwner = boatService.getBoatsByOwner(id);
        if (userAuthentication.isAdmin() || userAuthentication.getUser().equals(driver.getUsername())) {
            if (!boatsByOwner.isEmpty()) {
                model.addAttribute("error", "Cannot delete driver as there's boat which owned by that person");
                return "modelPerson/delete-driver";
            } else {
                List<Entry> boatsByPerson = entryService.getOpenEntriesByPerson(id);
                if (!boatsByPerson.isEmpty()) {
                    model.addAttribute(
                            "error", "Cannot delete driver as there's boat which driven by that person");
                } else {
                    driver.setDeleted(true);
                    personService.updatePerson(driver);
                }
                return "modelPerson/delete-driver";
            }
        } else {
            return "access-denied";
        }
    }
    /*

    @RequestMapping("/showFormForAdddriver")
    public String showForm(Model model){
        model.addAttribute("person", new driver());
        model.addAttribute("person", new driver());
        return "add-driver-form";
    }

    @GetMapping("/searchdrivers")
    public String searchdrivers(@RequestParam("theSearchName") String name, Model model) {
        List<Person> drivers = personService.searchdrivers(name);
        model.addAttribute("persons", drivers);
        return "list-drivers";
    }

     */

}
