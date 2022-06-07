package com.voc.raceEntry.Controller;

import com.voc.raceEntry.Entity.*;
import com.voc.raceEntry.Service.BoatService;
import com.voc.raceEntry.Service.DriverService;
import com.voc.raceEntry.Service.EntryService;
import com.voc.raceEntry.Service.EventService;
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

import javax.validation.Valid;
import java.io.*;
import java.util.List;

@Controller
@RequestMapping(value = "/entry")
public class EntryController {
    @Autowired
    private EntryService entryService;
    @Autowired
    private EventService eventService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private BoatService boatService;
    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
    @RequestMapping("/add/{eventId}")
    private String form(@PathVariable("eventId") long eventId, Model model) {
        EntryFormData entryFormData = new EntryFormData();
        entryFormData.setEntryId(-1);
        entryFormData.setEventId(eventId);
        model = loadFormData(model);
        model.addAttribute("entryFormData", entryFormData);
        return "modelEntry/add-entry-form";
    }
    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("entryFormData") EntryFormData entryFormData, BindingResult bindingResult, Model model) {
        System.out.println(bindingResult.toString());
        if (bindingResult.hasErrors()) {
            model = loadFormData(model);
            model.addAttribute("entryForData", entryFormData);
            return "modelEntry/add-entry-form";
        } else {
            System.out.println("===================");
            System.out.println(entryFormData.toString());
            System.out.println("===================");
            entryService.updateEntry(entryFormData);
            return "redirect:/event/list";
        }
    }
    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model){
        Entry entry = entryService.getEntry(id);
        EntryFormData entryFormData = new EntryFormData(entry);
        model = loadFormData(model);
        model.addAttribute("entryFormData", entryFormData);
        return "modelEntry/add-entry-form";
    }
    @RequestMapping("/entriesByEvent/{eventId}")
    public String entriesByEvent(@PathVariable(value = "eventId") long eventId, Model model) {
        List<Entry> entries = entryService.getEntriesByEvent(eventId);
        model.addAttribute("eventId",eventId);
        model.addAttribute("entries", entries);
        return "modelEntry/entries-by-event";
    }
    //https://stackoverflow.com/questions/5673260/downloading-a-file-from-spring-controllers
    @RequestMapping("/printForm/{id}")
    public ResponseEntity<byte[]> downloadForm(@PathVariable("id") Long id) throws IOException {

        //https://stackoverflow.com/questions/5673260/downloading-a-file-from-spring-controllers
        /*try {
            InputStream inputStream = stream.createInputStream();
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.setContentType("application/pdf");
            response.flushBuffer();
        } catch (IOException e) {
            throw  new RuntimeException("error in pdf to writting to input streams");
        }

         */
        Entry entry = entryService.getEntry(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename,filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        byte[] contents = entryService.generatePDF(entry);
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;
    }
    private Model loadFormData(Model model) {
        List<Event> events = eventService.getOpenEvents();
        List<Boat> boats = boatService.getBoats();
        List<Driver> drivers = driverService.getDrivers();
        model.addAttribute("events", events);
        model.addAttribute("boats", boats);
        model.addAttribute("drivers",drivers);
        return model;
    }
}
