package com.voc.raceEntry.Entity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "boat_view")
public class BoatView {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "boat_id")
    private long boatId;
    @NotNull(message = "You must enter in a boat name.")
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "race_no")
    private String raceNo;
    @Basic
    @Column(name = "class_name")
    private String className;
    @Basic
    @Column(name = "capacity_of_motor")
    private String capacityOfMotor;
    @NotNull(message = "Value must between 0 & 25")
    @Min(value = 1, message = "Value must between 0 & 25")
    @Max(value = 26, message = "Value must between 0 & 25")
    private BigDecimal length;
    @NotNull(message = "Value must between 0 & 25")
    @Min(value = 1, message = "Value must between 0 & 25")
    @Max(value = 26, message = "Value must between 0 & 25")
    private BigDecimal beam;
    @Min(value = 1, message = "You must select a owner")
    private Integer ownerId;
    //when it is a new boat
    public BoatView() {
    }

    public BoatView(Boat boat) {
        boatId = boat.getBoatId();
        name = boat.getName();
        raceNo = boat.getRaceNo();
        className = boat.getClassName();
        capacityOfMotor = boat.getCapacityOfMotor();
        length = boat.getLength();
        beam = boat.getBeam();
    }

    public Boat toBoat() {
        Boat boat = new Boat();
        if(boatId != -1)
            boat.setBoatId(boatId);
        boat.setName(name);
        boat.setRaceNo(raceNo);
        boat.setClassName(className);
        boat.setCapacityOfMotor(capacityOfMotor);
        boat.setLength(length);
        boat.setBeam(beam);
        return boat;
    }
    public long getBoatId() {
        return boatId;
    }

    public void setBoatId(long boatId) {
        this.boatId = boatId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaceNo() {
        return raceNo;
    }

    public void setRaceNo(String raceDno) {
        this.raceNo = raceDno;
    }


    public String getClassName() {

        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCapacityOfMotor() {
        return capacityOfMotor;
    }

    public void setCapacityOfMotor(String capacityOfMotor) {
        this.capacityOfMotor = capacityOfMotor;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getBeam() {
        return beam;
    }

    public void setBeam(BigDecimal beam) {
        this.beam = beam;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        String[] fields = {"name","race_no", "className","capacityOfMotor","length","beam","ownerId"};
        String[] fieldsNames = {"Name","race No", "Class Name","Capacity Of Motor","Length","Beam","Owner Name"};
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