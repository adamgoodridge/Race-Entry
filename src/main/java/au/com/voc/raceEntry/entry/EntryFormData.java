//demo only
package au.com.voc.raceEntry.entry;

//https://www.wimdeblauwe.com/blog/2021/04/16/using-html-select-options-with-thymeleaf/


import au.com.voc.raceEntry.user.User;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@FieldUnique.List({
        @FieldUnique(first = "firstDriverId", second = "secondDriverId", message = "The drivers cannot be the same")
})
@SecondSignature(message = "The second driver's signature and date is required")
public class EntryFormData {
    private long entryId;
    private long eventId;
    @Min(value = 1, message = "You must select a boat.")
    private long boatId;
    @Min(value = 1, message = "You must select a driver.")
    private long firstDriverId;
    private long secondDriverId;
    @NotNull(message = "You must select a class")
    private String boatClass;
    private User user;

    private String previousUrl;

    @NotNull(message = "Owner(s) signature's is required.")
    private String declarationOwnerSignature;

    @NotNull(message = "Owner(s) signature's date is required.")
    private String declarationOwnerDate;
    @NotNull(message = "Driver one's signature is required.")
    private String declarationDriverOneSignature;

    @NotNull(message = "Driver one's signature date is required.")
    private String declarationDriverOneDate;
    private String declarationDriverTwoSignature;
    private String declarationDriverTwoDate;

    public long getBoatId() {
        return boatId;
    }

    public EntryFormData() {

    }

    //when data need to edited from the data
    public EntryFormData(Entry entry) {
        eventId = entry.getEvent().getEventId();
        entryId = entry.getEntryId();
        boatId = entry.getBoat().getBoatId();
        firstDriverId = entry.getPersonOne().getPersonId();
        if (entry.getPersonTwo() != null) {
            secondDriverId = entry.getPersonTwo().getPersonId();
        }
        user = entry.getUser();
        boatClass = entry.getBoatClass();
        declarationOwnerSignature = entry.getDeclarationOwnerSignature();
        declarationOwnerDate = entry.getDeclarationOwnerDate();
        declarationDriverOneSignature = entry.getDeclarationDriverOneSignature();
        declarationDriverOneDate = entry.getDeclarationDriverOneDate();
        declarationDriverTwoSignature = entry.getDeclarationDriverTwoSignature();
        declarationDriverTwoDate = entry.getDeclarationDriverTwoDate();
    }

    public void setBoatId(long boatId) {
        this.boatId = boatId;
    }

    public long getFirstDriverId() {
        return firstDriverId;
    }

    public void setFirstDriverId(long firstDriverId) {
        this.firstDriverId = firstDriverId;
    }

    public long getSecondDriverId() {
        return secondDriverId;
    }

    public void setSecondDriverId(long secondDriverId) {
        this.secondDriverId = secondDriverId;
    }

    public long getEntryId() {
        return entryId;
    }

    public void setEntryId(long entryId) {
        this.entryId = entryId;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBoatClass() {
        return boatClass;
    }

    public void setBoatClass(String boatClass) {
        this.boatClass = boatClass;
    }

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public String getDeclarationOwnerSignature() {
        return declarationOwnerSignature;
    }

    public void setDeclarationOwnerSignature(String declarationOwnerSignature) {
        this.declarationOwnerSignature = declarationOwnerSignature;
    }

    public String getDeclarationOwnerDate() {
        return declarationOwnerDate;
    }

    public void setDeclarationOwnerDate(String declarationOwnerDate) {
        this.declarationOwnerDate = declarationOwnerDate;
    }

    public String getDeclarationDriverOneSignature() {
        return declarationDriverOneSignature;
    }

    public void setDeclarationDriverOneSignature(String declarationDriverOneSignature) {
        this.declarationDriverOneSignature = declarationDriverOneSignature;
    }

    public String getDeclarationDriverOneDate() {
        return declarationDriverOneDate;
    }

    public void setDeclarationDriverOneDate(String declarationDriverOneDate) {
        this.declarationDriverOneDate = declarationDriverOneDate;
    }

    public String getDeclarationDriverTwoSignature() {
        return declarationDriverTwoSignature;
    }

    public void setDeclarationDriverTwoSignature(String declarationDriverTwoSignature) {
        this.declarationDriverTwoSignature = declarationDriverTwoSignature;
    }

    public String getDeclarationDriverTwoDate() {
        return declarationDriverTwoDate;
    }

    public void setDeclarationDriverTwoDate(String declarationDriverTwoDate) {
        this.declarationDriverTwoDate = declarationDriverTwoDate;
    }

    @Override
    public String toString() {
        return "EntryFormData{" +
                "entryId=" + entryId +
                ", eventId=" + eventId +
                ", boatId=" + boatId +
                ", firstDriverId=" + firstDriverId +
                ", secondDriverId=" + secondDriverId +
                '}';
    }
}
