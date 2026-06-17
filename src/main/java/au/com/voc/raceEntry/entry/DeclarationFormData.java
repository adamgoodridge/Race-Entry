package au.com.voc.raceEntry.entry;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class DeclarationFormData {

    @NotNull
    private Long personId;

    @NotBlank
    private String signature;

    @NotNull
    private LocalDate signedDate;

    public DeclarationFormData() {}

    public Long getPersonId() { return personId; }
    public String getSignature() { return signature; }
    public LocalDate getSignedDate() { return signedDate; }

    public void setPersonId(Long personId) { this.personId = personId; }
    public void setSignature(String signature) { this.signature = signature; }
    public void setSignedDate(LocalDate signedDate) { this.signedDate = signedDate; }
}
