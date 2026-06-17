package au.com.voc.raceEntry.controller;

import au.com.voc.raceEntry.dashboard.DashboardService;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GeneralController {

    private final DashboardService dashboardService;
    private final UserService userService;

    public GeneralController(DashboardService dashboardService, UserService userService) {
        this.dashboardService = dashboardService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        if (userService.isAdmin()) {
            model.addAttribute("dashboard", dashboardService.getAdminDashboard());
            return "admin-home";
        }
        User user = userService.getCurrentUser();
        model.addAttribute("dashboard", dashboardService.getUserDashboard(user));
        return "home";
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "login/login-form";
    }
}
