package au.com.voc.raceEntry.user.register;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@FieldMatch.List({
        @FieldMatch(first = "password", second = "matchingPassword", message = "The password fields must match")
})

public class CrmUser {

    @Pattern(regexp = "(.+)@(.+).(.+)", message = "A valid email is required")
    private String email;

    @Size(min = 7, message = "The password must be at minimum 7 characters long")
    private String password;
    @Size(min = 7, message = "The password must be at minimum 7 characters long")

    private String matchingPassword;

    @NotNull(message = "First name is required")
    @Size(min = 1, message = "First name is required")
    private String firstName;
    @CodeMatch
    private String code;


    public String getEmail() {
        return email;
    }


    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
