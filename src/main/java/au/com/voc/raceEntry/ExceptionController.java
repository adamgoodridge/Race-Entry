package au.com.voc.raceEntry;

import au.com.voc.raceEntry.utils.DateFormatException;
import au.com.voc.raceEntry.utils.NotLoggedInException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;


@ControllerAdvice
public class ExceptionController {
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView notFound() {
        ModelAndView mav = new ModelAndView("error-message");
        mav.addObject("heading", "Http Status 404 - Page doesn't exists");
        mav.addObject("message", "The page that you request can't be found. You maybe made a typo or the page doesn't exist anyone.");
        return mav;
    }

    @ExceptionHandler(DateFormatException.class)
    public ModelAndView dataFormat() {
        ModelAndView mav = new ModelAndView("error-message");
        mav.addObject("heading", "Error");
        mav.addObject("message", "Date isn't format correctly");
        return mav;
    }

    @ExceptionHandler(NotLoggedInException.class)
    public ModelAndView notLoggedIn() {
        ModelAndView mav = new ModelAndView("login/login-form");
        return mav;
    }
}
