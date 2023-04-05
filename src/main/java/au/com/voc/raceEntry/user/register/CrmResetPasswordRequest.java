package au.com.voc.raceEntry.user.register;

import javax.validation.constraints.Pattern;

public class CrmResetPasswordRequest {

    @Pattern(regexp = "(.+)@(.+).(.+)", message = "A valid email is required.")
    private String email;

    public CrmResetPasswordRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
