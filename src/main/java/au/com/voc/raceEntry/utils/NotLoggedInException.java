package au.com.voc.raceEntry.utils;

public class NotLoggedInException extends RuntimeException {

    public NotLoggedInException(ClassCastException message) {
        super(message);
    }
}
