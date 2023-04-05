package au.com.voc.raceEntry.boat;


import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.entry.EntryService;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonService;
import au.com.voc.raceEntry.user.User;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/boat")
public class BoatController {

    private final BoatService boatService;
    private final PersonService personService;
    private final EntryService entryService;
    private final UserService userService;

    @Autowired
    public BoatController(BoatService boatService, PersonService personService, EntryService entryService, UserService userService) {
        this.boatService = boatService;
        this.personService = personService;
        this.entryService = entryService;
        this.userService = userService;
    }

    ///removes spaces at the start & end
    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @RequestMapping("/list")
    private String showBoats(Model model) {
        User user = userService.getCurrentUser();
        List<Boat> boats;
        if (user.isAdmin()) {
            boats = boatService.getAllBoats();
        } else {
            boats = boatService.getBoatsByUser(user);
        }
        model.addAttribute("boats", boats);
        return "modelBoat/list-boats";
    }

    @RequestMapping("/add")
    private String form(Model model, HttpServletRequest request) {
        User user = userService.getCurrentUser();
        BoatView boatView = new BoatView(user);
        String referer = request.getHeader("Referer");
        boatView.setPreviousUrl(referer);
        boatView.setUserId(user.getId());
        model.addAttribute("boatView", boatView);
        List<Person> drivers = personService.getPersonsByUser(user);
        model.addAttribute("persons", drivers);
        return "modelBoat/add-boat-form";
    }

    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("boatView") BoatView boatView, BindingResult bindingResult, Model model) {
        System.out.println(boatView.getOwnerId());
        User user = userService.getCurrentUser();
        if (boatView.getUserId().equals(user.getId()) || user.isAdmin()) {
            if (bindingResult.hasErrors()) {
                model.addAttribute("boatView", boatView);
                List<Person> drivers = personService.getAllPersons();
                model.addAttribute("persons", drivers);
                return "modelBoat/add-boat-form";
            } else {
                user = userService.findByUserId(boatView.getUserId());
                Boat boat = boatView.toBoat(user);
                boatService.updateBoat(boat, boatView.getOwnerId(), boatView.getUserId());
                return "redirect:/";
            }
        } else {
            return "access-denied";
        }
    }

    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model, HttpServletRequest request) {
        Boat boat = boatService.getBoat(id);
        User user = userService.getCurrentUser();
        if (boat.getUser().getId().equals(user.getId()) || user.isAdmin()) {
            BoatView boatView = new BoatView(boat);
            String referer = request.getHeader("Referer");
            boatView.setPreviousUrl(referer);
            model.addAttribute("boatView", boatView);
            List<Person> people = personService.getPersonsByUser(user);
            model.addAttribute("persons", people);
            return "modelBoat/add-boat-form";
        } else {
            return "access-denied";
        }
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable(value = "id") long id, Model model) {
        Boat boat = boatService.getBoat(id);
        User user = userService.getCurrentUser();
        if (boat.getUser().equals(user) || user.isAdmin()) {
            List<Entry> entries = entryService.getEntriesByBoatOpened(boat.getBoatId());
            if (!entries.isEmpty()) {
                model.addAttribute("error", "Cannot delete boat as there's events opened that the boat entered in");
            } else {
                boat.setDeleted(true);
                boatService.updateBoat(boat, boat.getOwner().getPersonId(), boat.getUser().getId());
            }
            return "modelBoat/delete-boat";
        } else {
            return "access-denied";
        }
    }
}
