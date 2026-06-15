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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/event")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;
    @Autowired
    private BoatClassService boatClassService;

    @RequestMapping("/list/opened")
    public String listOpenEvents(Model model) {
        List<EventFormData> events = eventService.getEventsView(1);
        model.addAttribute("eventsView", events);
        return "modelEvent/list-events";
    }

    @RequestMapping("/list/closed")
    public String listClosedEvents(Model model) {
        List<EventFormData> events = eventService.getEventsView(0);
        model.addAttribute("eventsView", events);
        return "modelEvent/list-closed-events";
    }

    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("event") Event event, BindingResult bindingResult, Model model) {
        log.debug("=============={}", event.getOpen());
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors: {}", bindingResult.getAllErrors());
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
        List<EventFormData> events = eventService.getEventsView(0);
        model.addAttribute("eventsView", events);
        return "modelEvent/list-events";
    }

    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model) {
        Event event = eventService.getEvent(id);
        model.addAttribute("event", event);
        return "modelEvent/add-event-form";
    }

    @RequestMapping("/add")
    public String add(Model model) {
        Event event = new Event();
        event.setEventId(-1L);
        model.addAttribute("event", event);
        model.addAttribute("boatClasses", boatClassService.getBoatClasses());
        return "modelEvent/add-event-form";
    }
}