//demo only
package au.com.voc.raceEntry.event;

import au.com.voc.raceEntry.boat_class.BoatClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;
    @Autowired
    private BoatClassService boatClassService;

    @RequestMapping("/list/opened")
    public String listOpenEvents(Model model) {
        List<EventView> events = eventService.getEventsView(1);
        model.addAttribute("eventsView", events);
        return "modelEvent/list-events";
    }

    @RequestMapping("/list/closed")
    public String listClosedEvents(Model model) {
        List<EventView> events = eventService.getEventsView(0);
        model.addAttribute("eventsView", events);
        return "modelEvent/list-closed-events";
    }

    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("event") Event event, BindingResult bindingResult, Model model) {
        System.out.println("==============" + event.getOpen());
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            model.addAttribute("event", event);
            return "modelEvent/add-event-form";
        } else {
            eventService.updateEvent(event);
            return "redirect:/event/list/opened";
        }
    }

    @RequestMapping("/delete/{id}")
    public String deleteEvent(@PathVariable(value = "id") long id, Model model) {
        try {
            eventService.deleteEvent(id);
        } catch (RuntimeException exception) {
            model.addAttribute("error", exception.getMessage());
        }
        List<EventView> events = eventService.getEventsView(0);
        model.addAttribute("eventsView", events);
        return "modelEvent/list-events";
    }

    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model, HttpServletRequest request) {
        Event event = eventService.getEvent(id);
        String referer = request.getHeader("Referer");
        event.setPreviousUrl(referer);

        model.addAttribute("event", event);
        return "modelEvent/add-event-form";
    }

    @RequestMapping("/add")
    public String add(Model model, HttpServletRequest request) {
        Event event = new Event();
        long id = -1;
        event.setEventId(id);
        String referer = request.getHeader("Referer");
        event.setPreviousUrl(referer);
        model.addAttribute("event", event);
        model.addAttribute("boatClasses", boatClassService.getBoatClasses());
        return "modelEvent/add-event-form";
    }
}