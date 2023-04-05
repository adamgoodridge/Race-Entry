package au.com.voc.raceEntry.person;


import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.utils.LicenceExpiry;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@SuppressWarnings("unused")
@Entity
public class Person implements LicenceExpiry {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "driver_id")
    private Long driverId;

    @NotNull(message = "First name is required")
    @Size(min = 1, message = "First name is must between 1 & 30 characters long")
    @Column(name = "first_name")
    @ColumnTransformer(
            read = "AES_DECRYPT(first_name, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 1, message = "Last name is must between 1 & 30 characters long")
    @ColumnTransformer(
            read = "AES_DECRYPT(last_name, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "Enter a valid email")
    @Pattern(regexp = "[a-zA-Z\\d-_]+@[a-zA-Z\\d-._]+[.].[a-zA-Z\\d-_]*$", message = "Enter a valid email")
    @Column(name = "email")
    @ColumnTransformer(
            read = "AES_DECRYPT(email, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    private String email;
    @NotNull(message = "A.P.B.A Licence number is required")
    @Min(value = 1, message = "A.P.B.A Licence number is required")
    @Column(name = "apba_licence_no")
    @ColumnTransformer(
            read = "AES_DECRYPT(apba_licence_no, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    private Integer apbaLicence;
    @NotNull(message = "SBA Licence number is required")
    @Column(name = "sba_licence")
    @ColumnTransformer(
            read = "AES_DECRYPT(sba_licence, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    private String sbaLicence;
    @Column(name = "sba_date")
    @NotNull(message = "SBA Licence expiry date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sbaLicenceExpiryDate;
    @Basic
    @Column(columnDefinition = "VARCHAR", name = "phone_no")
    @ColumnTransformer(
            read = "AES_DECRYPT(phone_no, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @NotNull(message = "Phone Number is required")
    private String phoneNo;
    @Column(columnDefinition = "VARCHAR", name = "address_line_one")
    @ColumnTransformer(
            read = "AES_DECRYPT(address_line_one, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @NotNull(message = "Address is required")
    private String addressLineOne;
    @Basic
    @Column(columnDefinition = "VARCHAR", name = "address_line_two")
    @ColumnTransformer(
            read = "AES_DECRYPT(address_line_two, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    private String addressLineTwo;
    @Basic
    @Column(columnDefinition = "VARCHAR", name = "address_city")
    @ColumnTransformer(
            read = "AES_DECRYPT(address_city, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @NotNull(message = "City is required")
    private String addressCity;
    @Basic
    @Column(columnDefinition = "VARCHAR", name = "address_state")
    @ColumnTransformer(
            read = "AES_DECRYPT(address_state, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @NotNull(message = "State is required")
    private String addressState;


    @Column(name = "address_postcode")
    @ColumnTransformer(
            read = "AES_DECRYPT(address_postcode, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    private String addressPostCode;

    @Basic
    @Column(name = "club_name")
    private String clubName;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;
    @Transient
    private Long userIId;
    @Transient
    private String username;


    @Column(name = "deleted", nullable = false, columnDefinition = "BIT", length = 1)
    private Boolean deleted;

    //not a actual column

    @Transient
    private String previousUrl;

    public Long getPersonId() {
        return driverId;
    }

    public void setPersonId(Long driverId) {
        this.driverId = driverId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String driverFirstName) {
        this.firstName = driverFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String driverLastName) {
        this.lastName = driverLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String driverEmail) {
        this.email = driverEmail;
    }

    public String getSbaLicence() {
        return sbaLicence;
    }

    public void setSbaLicence(String sbaLicence) {
        this.sbaLicence = sbaLicence;
    }

    public void setAddressPostCode(String addressPostCode) {
        this.addressPostCode = addressPostCode;
    }

    public Long getUserIId() {
        return userIId;
    }

    public void setUserIId(Long userIId) {
        this.userIId = userIId;
    }

    public String getPreviousUrl() {
        return previousUrl;
    }

    public void setPreviousUrl(String previousUrl) {
        this.previousUrl = previousUrl;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String driverPhoneNo) {
        this.phoneNo = driverPhoneNo;
    }

    public String getAddressLineOne() {
        return addressLineOne;
    }

    public void setAddressLineOne(String driverAddressLineOne) {
        this.addressLineOne = driverAddressLineOne;
    }

    public String getAddressLineTwo() {
        return addressLineTwo;
    }

    public void setAddressLineTwo(String driverAddressLineTwo) {
        this.addressLineTwo = driverAddressLineTwo;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String driverAddressCity) {
        this.addressCity = driverAddressCity;
    }

    public Integer getAddressPostCode() {
        if (addressPostCode != null) {
            return Integer.parseInt(addressPostCode);
        } else {
            return 0;
        }
    }

    public Long getUserId() {
        return userIId;
    }

    public void setUserId(Long userIId) {
        this.userIId = userIId;
    }

    public void setAddressPostCode(Integer driverAddressPost) {
        this.addressPostCode = String.valueOf(driverAddressPost);
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }


    public void setApbaLicence(int apbaLicence) {
        this.apbaLicence = apbaLicence;
    }

    public Integer getApbaLicence() {
        return apbaLicence;
    }


    @Override
    @Deprecated
    public String toString() {
        return "Person{" +
                "driverId=" + driverId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", sbaLicence='" + sbaLicence + '\'' +
                ", sbaLicenceExpiryDate='" + sbaLicenceExpiryDate + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", addressLineOne='" + addressLineOne + '\'' +
                ", addressLineTwo='" + addressLineTwo + '\'' +
                ", addressCity='" + addressCity + '\'' +
                ", addressState='" + addressState + '\'' +
                ", club Name=" + clubName +
                '}';
    }

    public String getFullAddress() {
        String output = addressLineOne + ", ";
        if (addressLineTwo != null)
            output += addressLineTwo + ", ";
        output += addressCity + ", " + addressState + " " + addressPostCode;
        return output;
    }

    public void setApbaLicence(Integer apbaLicence) {
        this.apbaLicence = apbaLicence;
    }

    public LocalDate getSbaLicenceExpiryDate() {
        return sbaLicenceExpiryDate;
    }

    public void setsbaLicenceExpiryDate(LocalDate sbaLicenceExpiryDate) {
        this.sbaLicenceExpiryDate = sbaLicenceExpiryDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //Generates code for thymeleaf
    public static void main(String[] args) {
        String[] fields = {"firstName", "lastName", "email", "sbaLicence", "PhoneNo", "addressLineOne", "addressLineTwo", "addressCity", "addressState", "addressPostCode"};
        //Names that will appear in the web based interface
        String[] fieldsNames = {"First Name", "Last Name", "Email", "S.B.A Licence", "PhoneNo", "Address Line One", "Address Line Two", "City", "State", "PostCode"};
        for (int i = 0; i < fields.length; i++) {
            System.out.println("<div class=\"mb-3\">\n");
            System.out.println("<label class=\"form-label\">Enter your " + fieldsNames[i] + " *</label>\n");
            System.out.println("<input type=\"text\" th:field=\"*{" + fields[i] + "}\" class=\"form-control\" >\n");
            System.out.println("<p th:if=\"${#fields.hasErrors('" + fields[i] + "')}\" th:errorclass=\"error\" th:errors=\"*{" + fields[i] + "}\" />\n");
            System.out.println("</div>\n");
        }
    }
}
