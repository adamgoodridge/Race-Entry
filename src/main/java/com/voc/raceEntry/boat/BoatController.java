package com.voc.raceEntry.boat;


import com.voc.raceEntry.driver.Driver;
import com.voc.raceEntry.driver.DriverService;
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

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/boat")
public class BoatController {
    @Autowired
    private BoatService boatService;
    @Autowired
    private DriverService driverService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
    @RequestMapping("/list")
    private String showBoats(Model model) {
        List<Boat> boats = boatService.getBoats();
        System.out.println("size="+boats.size());
        model.addAttribute("boats",boats);
        return "modelBoat/list-boats";
    }
    @RequestMapping("/add")
    private String form(Model model) {
        BoatView boatView = new BoatView();
        boatView.setBoatId(-1);
        model.addAttribute("boatView", boatView);
        List<Driver> drivers = driverService.getDrivers();
        model.addAttribute("drivers",drivers);
        return "modelBoat/add-boat-form";
    }
    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("boatView") BoatView boatView, BindingResult bindingResult, Model model) {
        System.out.println(bindingResult.toString());
        if (bindingResult.hasErrors()) {
            model.addAttribute("boatView", boatView);
            List<Driver> drivers = driverService.getDrivers();
            model.addAttribute("drivers",drivers);
            return "modelBoat/add-boat-form";
        } else {
            boatService.updateBoat(boatView);
            return "redirect:/boat/list";
        }
    }
    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model){
        Boat boat = boatService.getBoat(id);
        BoatView boatView = new BoatView(boat);
        System.out.println(boatView.getOwnerId() + ">>>>>>>>>>>>>>>>>>>>>>");
        model.addAttribute("boatView", boatView);
        List<Driver> drivers = driverService.getDrivers();
        model.addAttribute("drivers",drivers);
        return "modelBoat/add-boat-form";
    }
}
