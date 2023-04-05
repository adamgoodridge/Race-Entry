package au.com.voc.raceEntry.user;


import au.com.voc.raceEntry.user.register.CrmPasswordUser;
import au.com.voc.raceEntry.user.register.CrmResetPasswordRequest;
import au.com.voc.raceEntry.user.register.CrmUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/showRegistrationForm")
    public String showMyRegistrationForm(Model model) {
        model.addAttribute("crmUser", new CrmUser());
        return "login/registration-form";

    }

    @RequestMapping("/processRegistrationForm")
    public String processMyRegistrationForm(@Valid @ModelAttribute("crmUser") CrmUser crmUser,
                                            BindingResult bindingResult, Model model) {
        //form validation
        if (bindingResult.hasErrors()) {
            crmUser.setPassword("");
            crmUser.setMatchingPassword("");
            model.addAttribute("crmUser", crmUser);
            return "login/registration-form";
        }
        //check username already taken
        User existing = userService.findByEmail(crmUser.getEmail());
        if (existing != null) {
            bindingResult.rejectValue("email", null, "There is already is an account with that username");
            model.addAttribute("crmUser", crmUser);
            return "login/registration-form";
        }
        userService.save(crmUser);
        return "redirect:/login?registered";
    }

    @RequestMapping("/resetPassword")
    public String resetPassword(Model model) {
        CrmResetPasswordRequest crmResetPasswordRequest = new CrmResetPasswordRequest();
        model.addAttribute("crmResetPasswordRequest", crmResetPasswordRequest);
        return "login/reset-password-request-form";
    }

    @RequestMapping("/processResetRequestForm")
    public String resetPasswordProcess(@Valid @ModelAttribute("crmResetPasswordRequest") CrmResetPasswordRequest crmResetPasswordRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("crmResetPasswordRequest", crmResetPasswordRequest);
            return "login/reset-password-request-form";
        } else {
            userService.sendPasswordRecovery(crmResetPasswordRequest);
            return "redirect:/login?resetPasswordRequest";
        }
    }

    @RequestMapping("/recovery/{userId}/{code}")
    public String resetPassword(@PathVariable("userId") Long userId, @PathVariable("code") String code, Model model) {
        User user;
        try {
            user = userService.findByUserId(userId);
        } catch (EntityNotFoundException entityNotFoundException) {
            return "redirect:/login?invalidRecovery";
        }
        //can't compared a null value
        if (user == null || user.getResetCode() == null || !user.getResetCode().equals(code)) {
            return "redirect:/login?invalidRecovery";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date now = new Date();
        System.out.println("======" + now);
        if (now.compareTo(user.getResetDate()) < 1) {
            CrmPasswordUser crmPasswordUser = new CrmPasswordUser();
            crmPasswordUser.setUserId(user.getId());
            crmPasswordUser.setCode(user.getResetCode());
            model.addAttribute("crmPasswordUser", crmPasswordUser);
            return "login/reset-form";
        } else {
            return "redirect:/login?expiredRecovery";
        }
    }

    @RequestMapping("/passwordRequestProcess")
    public String resetPassword(@Valid @ModelAttribute("crmPasswordUser") CrmPasswordUser crmPasswordUser, BindingResult bindingResult, Model model) {
        //form validation
        if (bindingResult.hasErrors()) {
            crmPasswordUser.setPassword("");
            crmPasswordUser.setMatchingPassword("");
            model.addAttribute("crmPasswordUser", crmPasswordUser);
            return "login/registration-form";
        }
        User user = userService.findByUserId(crmPasswordUser.getUserId());
        System.out.println(user.getUsername());
        System.out.println(user.getUsername());
        System.out.println(user.getUsername());
        if (user == null || !user.getResetCode().equals(crmPasswordUser.getCode())) {
            return "redirect:/login?invalidRecovery";
        } else {
            user.setPassword(crmPasswordUser.getPassword());
            userService.updatePassword(user);
            return "redirect:/login?updatedPassword";
        }
    }

    @RequestMapping("/settings")
    public String settings(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "login/settings";
    }

    //qr https://www.baeldung.com/spring-security-two-factor-authentication-with-soft-token
    @RequestMapping("/2fa")
    public String generateQR() {
        User user = userService.getCurrentUser();
        if (user.isUsing2Fa()) {
            return "login/qrcode-overwrite";
        } else {
            return "redirect:/user/qr/generate";
        }
    }

    @RequestMapping("qr/generate")
    public String generateQr(Model model) throws UnsupportedEncodingException {
        User user = userService.generateUser2FA();
        String qr = userService.generateQRUrl(user);
        model.addAttribute("qr", qr);

        return "login/qrcode";
    }

    @RequestMapping("/2fa/confirm")
    public String confirm2Fa() {
        userService.confirm2Fa();
        return "redirect:/user/settings";
    }

    @RequestMapping("/2fa/cancel")
    public String cancel2Fa() {
        userService.cancel2Fa();
        return "redirect:/user/settings";
    }
}