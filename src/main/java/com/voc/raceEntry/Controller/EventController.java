package com.voc.raceEntry.Controller;

import com.voc.raceEntry.Entity.Event;
import com.voc.raceEntry.Entity.EventView;
import com.voc.raceEntry.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @RequestMapping("/list")
    public String listEvents(Model model) {
        List<EventView> events = eventService.getOpenEventsView();
        model.addAttribute("eventsView", events);
        return "modelEvent/list-events";
    }
    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("event") Event event, BindingResult bindingResult, Model model) {
        System.out.println(bindingResult.toString());
        if (bindingResult.hasErrors()) {
            model.addAttribute("event", event);
            return "modelEvent/add-event-form";
        } else {
            eventService.updateEvent(event);
            return "redirect:/event/list";
        }
    }


    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model){
        Event event = eventService.getEvent(id);
        model.addAttribute("event", event);
        return "modelEvent/add-event-form";
    }
    @RequestMapping("/add")
    public String add(Model model) {
        Event event = new Event();
        long id = -1;
        event.setEventId(id);
        model.addAttribute("event", event);
        return "modelEvent/add-event-form";
    }
}