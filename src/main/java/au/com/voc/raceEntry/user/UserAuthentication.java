package au.com.voc.raceEntry.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuthentication {
    private final Authentication authentication;

    public UserAuthentication() {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isAdmin() {
        System.out.println(getUser());
        return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
    }

    public String getUser() {
        return authentication.getName();
    }


}
