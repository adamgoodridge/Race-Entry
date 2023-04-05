package au.com.voc.boatEntry.test;

import au.com.voc.raceEntry.RaceEntryApplication;
import au.com.voc.raceEntry.event.Event;
import au.com.voc.raceEntry.event.EventRepository;
import au.com.voc.raceEntry.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RaceEntryApplication.class
)
public class EventTableTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    @Transactional
    public void validEventDateById() {
        Long id = Long.parseLong("2");
        Event event = eventRepository.getReferenceById(id);
        LocalDate excepted = DateUtils.format("01/01/2023");
        assertEquals(excepted, event.getStartDate());
    }
}
