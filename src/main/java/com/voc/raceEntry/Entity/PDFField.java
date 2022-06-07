package com.voc.raceEntry.Entity;

public class PDFField {
    private String name, value;

    public PDFField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
