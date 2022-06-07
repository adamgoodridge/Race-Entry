package com.voc.raceEntry.Controller;

import com.voc.raceEntry.Entity.Driver;
import com.voc.raceEntry.Service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }
    @RequestMapping("/add")
    public String showFormDriver(Model model){
        Driver driver = new Driver();
        driver.setDriverId(-1);
        model.addAttribute("driver", driver);
        return "modelDriver/add-driver-form";
    }
    @RequestMapping("/processForm")
    public String processForm(@Valid @ModelAttribute("driver") Driver driver, BindingResult bindingResult) {
        System.out.println("Binding Result" + bindingResult.toString());
        System.out.println("The FirstName  |"+driver.getFirstName()+"|");

        if (bindingResult.hasErrors()) {
            return "modelDriver/add-driver-form";
        } else {
               driverService.updateDriver(driver);
            return "redirect:/driver/list";
        }
    }
    @RequestMapping("/list")
    public String listDrivers(Model model) {
        List<Driver> drivers = driverService.getDrivers();
        model.addAttribute("drivers", drivers);
        return "modelDriver/list-drivers";
    }
    @RequestMapping("/update/{id}")
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model){
        Driver driver = driverService.getDriver(id);
        model.addAttribute("driver", driver);
        return "modelDriver/add-driver-form";
    }
    @RequestMapping("/delete/{id}")
    public String deleteDriver(@PathVariable("id") long id){
        driverService.deleteDriver(id);
        return "redirect:/driver/list";
    }
    /*

    @RequestMapping("/showFormForAdddriver")
    public String showForm(Model model){
        model.addAttribute("driver", new driver());
        return "add-driver-form";
    }

    @GetMapping("/searchdrivers")
    public String searchdrivers(@RequestParam("theSearchName") String name, Model model) {
        List<Driver> drivers = driverService.searchdrivers(name);
        model.addAttribute("drivers", drivers);
        return "list-drivers";
    }

     */

}
