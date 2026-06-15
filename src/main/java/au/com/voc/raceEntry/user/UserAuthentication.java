package au.com.voc.raceEntry.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class UserAuthentication {

    private static final Logger log = LoggerFactory.getLogger(UserAuthentication.class);

    private final Authentication authentication;

    public UserAuthentication() {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isAdmin() {
        log.debug("{}", getUser());
        return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
    }

    public String getUser() {
        return authentication.getName();
    }


}
