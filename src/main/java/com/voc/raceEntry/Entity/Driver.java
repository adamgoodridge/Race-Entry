package com.voc.raceEntry.Entity;


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

    @NotNull(message="Last name is required")
    @Size(min=1, message="Last name is must between 1 & 30 characters long")
    @Column(name = "last_name")
    private String lastName;

    @NotNull(message ="Enter a valid email")
    @Pattern(regexp = "[a-zA-Z0-9-_]+@[a-zA-Z0-9-._]+[.].[a-zA-Z0-9-_]*$", message ="Enter a valid email")
    @Column(name = "email")
    private String email;
    @Basic
    @Column(name = "sba_licence")
    private String sbaLicence;
    @Basic
    @Column(name = "sba_date")
    private String sbaDate;
    @Basic
    @Column(name = "phone_no")
    private String phoneNo;
    @Basic
    @Column(name = "address_line_one")
    private String addressLineOne;
    @Basic
    @Column(name = "address_line_two")
    private String addressLineTwo;
    @Basic
    @Column(name = "address_city")
    private String addressCity;
    @Basic
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

    public String getSbaLicence() {
        return sbaLicence;
    }

    public void setSbaLicence(String driverSbaLicence) {
        this.sbaLicence = driverSbaLicence;
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

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getSbaDate() {
        return sbaDate;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "driverId=" + driverId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", sbaLicence='" + sbaLicence + '\'' +
                ", sbaDate='" + sbaDate + '\'' +
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
        if(addressLineTwo != null)
            output += addressLineTwo + ", ";
        output += addressCity + ", " + addressState + " " + addressPostCode;
        return output;
    }

    public void setSbaDate(String sbaDate) {
        this.sbaDate = sbaDate;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        String[] fields = {"firstName","lastName", "email","sbaLicence","PhoneNo","addressLineOne","addressLineTwo","addressCity","addressState","addressPostCode"};
        String[] fieldsNames = {"First Name","Last Name", "Email","S.B.A Licence","PhoneNo","Address Line One","Address Line Two","City","State","PostCode"};
        for(int i = 0; i < fields.length; i++) {
            /*
            sb.append("<div class=\"mb-3\">");
            sb.append("\n<label for=\"" +fields[i] + "\" class=\"form-label\">Enter your "+ fieldsNames[i] + "</label>");
            sb.append("\n<form:input path=\""+ fields[i] +"\" id=\""+ fields[i] +"\" class=\"form-control\" /><form:errors path=\""
                    + fields[i] +"\" cssClass=\"error\">");
            sb.append("\n</div>");

             */
            sb.append("<div class=\"mb-3\">\n");
            sb.append("<label class=\"form-label\">Enter your "+ fieldsNames[i] +" *</label>\n");
            sb.append("<input type=\"text\" th:field=\"*{"+fields[i]+"}\" class=\"form-control\" >\n");
            sb.append("<p th:if=\"${#fields.hasErrors('"+fields[i]+"\')}\" th:errorclass=\"error\" th:errors=\"*{"+fields[i]+"}\" />\n");
            sb.append("</div>\n");
        }
        System.out.println(sb.toString());
    }
}
