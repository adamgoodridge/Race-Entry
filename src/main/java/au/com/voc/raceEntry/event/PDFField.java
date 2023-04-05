package au.com.voc.raceEntry.event;

public class PDFField {
    private final String name;
    private final String value;

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
