package au.com.voc.boatEntry.test;


import au.com.voc.raceEntry.RaceEntryApplication;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RaceEntryApplication.class
)
public class PersonTest {
    private Validator validator;
    @Autowired
    private PersonRepository personRepository;

    @BeforeClass
    public void setUp() {
        System.out.println("hi");
    }

    public void close() {

    }

    @Test
    public void testEmailInvalidJustWord() {
        // I'd name the test to something like
        // invalidEmailShouldFailValidation()

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Person person = new Person();
        person.setEmail("ooas");
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        for (ConstraintViolation<Person> violation : violations) {
            System.out.println(violation.getMessage());
        }
        ConstraintViolation<Person> violation = violations.stream().filter(p -> p.getMessage().equals("Enter a valid email")).findFirst().orElse(null);
        assertNotNull(violation);
    }

    @Test
    public void testEmailSuccess() {
        // I'd name the test to something like
        // invalidEmailShouldFailValidation()

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Person person = new Person();
        person.setEmail("ooas@gmaui.com");
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        for (ConstraintViolation<Person> violation : violations) {
            System.out.println(violation.getMessage());
        }
        ConstraintViolation<Person> violation = violations.stream().filter(p -> p.getMessage().equals("Enter a valid email")).findFirst().orElse(null);
        assertNull(violation);
    }

    @Test
    public void testFindAll() {
        // I'd name the test to something like
        // invalidEmailShouldFailValidation()
        List<Person> people = personRepository.findAll();
        assertFalse(people.isEmpty());
    }

}