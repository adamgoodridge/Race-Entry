package com.voc.raceEntry.driver;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Objects;



@Entity
public class Driver {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "driver_id")
    private Long driverId;

    @NotNull(message="First name is required")
    @Size(min=1, message="First name is must between 1 & 30 characters long")
    @Column(name = "first_name")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 1, message = "Last name is must between 1 & 30 characters long")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message = "Enter a valid email")
    @Pattern(regexp = "[a-zA-Z0-9-_]+@[a-zA-Z0-9-._]+[.].[a-zA-Z0-9-_]*$", message = "Enter a valid email")
    @Column(name = "email")
    private String email;
    @NotNull(message = "A.P.B.A Licence number is required")
    @Min(value = 1, message = "A.P.B.A Licence number is required")
    @Column(name = "apba_licence_no")
    private Integer apbaLicence;
    @Basic
    @Column(name = "sba_licence")
    private String sbaLicence;
    @Basic
    @Column(name = "sba_date")
    private String sbaLicenceExpiry;
    @Basic
    @NotNull(message = "Phone Number is required")
    @Column(name = "phone_no")
    private String phoneNo;
    @Basic
    @NotNull(message = "Address is required")
    @Column(name = "address_line_one")
    private String addressLineOne;
    @Basic
    @Column(name = "address_line_two")
    private String addressLineTwo;
    @Basic
    @NotNull(message = "City is required")
    @Column(name = "address_city")
    private String addressCity;
    @Basic
    @NotNull(message = "State is required")
    @Column(name = "address_state")
    private String addressState;
    @NotNull(message = "Post Code must be between 1000 and 6000")
    @Min(value = 1000, message = "Post Code must be between 1000 and 6000")
    @Max(value = 6000, message = "Post Code must be between 1000 and 6000")
    @Column(name = "address_postcode")
    private Integer addressPostCode;
    @Basic
    @Column(name = "club_id")
    private Integer clubId;
    @Basic
    @Column(name = "user_id")
    private Integer userId;

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
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

    public String getsbaLicence() {
        return sbaLicence;
    }

    public void setsbaLicence(String driversbaLicence) {
        this.sbaLicence = driversbaLicence;
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
        return addressPostCode;
    }

    public void setAddressPostCode(Integer driverAddressPost) {
        this.addressPostCode = driverAddressPost;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getFullName(){
        return firstName+" "+lastName;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver driver = (Driver) o;
        return driverId == driver.driverId && Objects.equals(firstName, driver.firstName) && Objects.equals(lastName, driver.lastName) && Objects.equals(email, driver.email) && Objects.equals(sbaLicence, driver.sbaLicence) && Objects.equals(phoneNo, driver.phoneNo) && Objects.equals(addressLineOne, driver.addressLineOne) && Objects.equals(addressLineTwo, driver.addressLineTwo) && Objects.equals(addressCity, driver.addressCity) && Objects.equals(addressPostCode, driver.addressPostCode) && Objects.equals(clubId, driver.clubId) && Objects.equals(userId, driver.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, firstName, lastName, email, sbaLicence, phoneNo, addressLineOne, addressLineTwo, addressCity, addressPostCode, clubId, userId);
    }

    public void setApbaLicence(int apbaLicence) {
        this.apbaLicence = apbaLicence;
    }

    public Integer getApbaLicence() {
        return apbaLicence;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getsbaLicenceExpiry() {
        return sbaLicenceExpiry;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driverId=" + driverId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", sbaLicence='" + sbaLicence + '\'' +
                ", sbaLicenceExpiry='" + sbaLicenceExpiry + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", addressLineOne='" + addressLineOne + '\'' +
                ", addressLineTwo='" + addressLineTwo + '\'' +
                ", addressCity='" + addressCity + '\'' +
                ", addressState='" + addressState + '\'' +
                ", addressPostCode=" + addressPostCode +
                ", clubId=" + clubId +
                ", userId=" + userId +
                '}';
    }

    public String getFullAddress() {
        String output = addressLineOne + ", ";
        if (addressLineTwo != null)
            output += addressLineTwo + ", ";
        output += addressCity + ", " + addressState + " " + addressPostCode;
        return output;
    }

    public void setsbaLicenceExpiry(String sbaLicenceExpiry) {
        this.sbaLicenceExpiry = sbaLicenceExpiry;
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
