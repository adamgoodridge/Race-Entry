package au.com.voc.boatEntry.test;

import au.com.voc.raceEntry.RaceEntryApplication;
import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.utils.CheckExpiryValidator;
import au.com.voc.raceEntry.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RaceEntryApplication.class
)
public class CheckExpiryValidatorIntegrationTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    @Transactional
    public void validOneDriver() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Boat boat = new Boat();
        boat.setSbaLicenceDate(DateUtils.format("12/01/2023"));
        Person driver = new Person();
        driver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        //put into entry
        Entry entry = new Entry();
        entry.setBoat(boat);
        entry.setDriverOne(driver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        assertTrue(validator.isValid());
    }

    @Test
    @Transactional
    public void validTwoDrivers() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Boat boat = new Boat();
        boat.setSbaLicenceDate(DateUtils.format("12/01/2023"));
        Person firstDriver = new Person();
        firstDriver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        Person secondDriver = new Person();
        secondDriver.setsbaLicenceExpiryDate(DateUtils.format("12/06/2023"));
        //put into entry
        Entry entry = new Entry();
        entry.setBoat(boat);
        entry.setDriverOne(firstDriver);
        entry.setDriverTwo(secondDriver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        assertTrue(validator.isValid());
    }

    @Test
    @Transactional
    public void validOneDriverWithNoBoat() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Person driver = new Person();
        driver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        //put into entry
        Entry entry = new Entry();
        entry.setDriverOne(driver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        assertTrue(validator.isValid());
    }

    @Test
    @Transactional
    public void validTwoDriversWithNoBoat() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Person firstDriver = new Person();
        firstDriver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        Person secondDriver = new Person();
        secondDriver.setsbaLicenceExpiryDate(DateUtils.format("12/06/2023"));
        //put into entry
        Entry entry = new Entry();
        entry.setDriverOne(firstDriver);
        entry.setDriverTwo(secondDriver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        assertTrue(validator.isValid());
    }

    @Test
    @Transactional
    public void validOneDriverBoatNotValidSameDate() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Boat boat = new Boat();
        boat.setSbaLicenceDate(DateUtils.format("01/01/2023"));
        Person driver = new Person();
        driver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        //put into entry
        Entry entry = new Entry();
        entry.setBoat(boat);
        entry.setDriverOne(driver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        assertFalse(validator.isValid());
    }

    @Test
    @Transactional
    public void validOneDriverBoatNotValidEarlierDate() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Boat boat = new Boat();

        boat.setSbaLicenceDate(DateUtils.format("01/01/2022"));
        Person driver = new Person();
        driver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        //put into entry
        Entry entry = new Entry();
        entry.setBoat(boat);
        entry.setDriverOne(driver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        String exceptedMessage = "Boat's S.B.A Licence expires on 01/01/2022 and needs to updating as it expires before the event.";
        assertEquals(exceptedMessage, validator.getMessage());
    }

    @Test
    @Transactional
    public void invalidTwoDriversSecondDriver() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Boat boat = new Boat();
        boat.setSbaLicenceDate(DateUtils.format("12/01/2023"));
        Person firstDriver = new Person();
        firstDriver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        Person secondDriver = new Person();
        secondDriver.setsbaLicenceExpiryDate(DateUtils.format("12/06/2022"));
        secondDriver.setFirstName("Harold");
        secondDriver.setLastName("Smith");
        //put into entry
        Entry entry = new Entry();
        entry.setBoat(boat);
        entry.setDriverOne(firstDriver);
        entry.setDriverTwo(secondDriver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        String exceptedMessage = "The second driver Harold Smith's S.B.A licence expires on 12/06/2022 and needs to updating as it expires before the event.";
        assertEquals(exceptedMessage, validator.getMessage());
    }

    @Test
    @Transactional
    public void invalidTwoDriversFirstDriver() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        Boat boat = new Boat();
        boat.setSbaLicenceDate(DateUtils.format("12/01/2023"));
        Person firstDriver = new Person();
        firstDriver.setsbaLicenceExpiryDate(DateUtils.format("12/01/2023"));

        Person secondDriver = new Person();
        secondDriver.setsbaLicenceExpiryDate(DateUtils.format("12/06/2022"));
        secondDriver.setFirstName("Harold");
        secondDriver.setLastName("Smith");
        //put into entry
        Entry entry = new Entry();
        entry.setBoat(boat);
        entry.setDriverOne(secondDriver);
        entry.setDriverTwo(firstDriver);
        entry.setEvent(event);
        CheckExpiryValidator validator = new CheckExpiryValidator(entry);
        String exceptedMessage = "The first driver Harold Smith's S.B.A licence expires on 12/06/2022 and needs to updating as it expires before the event.";
        assertEquals(exceptedMessage, validator.getMessage());
    }
}
