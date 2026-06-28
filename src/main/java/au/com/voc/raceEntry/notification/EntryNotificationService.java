package au.com.voc.raceEntry.notification;

import au.com.voc.raceEntry.boat.Boat;
import au.com.voc.raceEntry.boat.BoatRepository;
import au.com.voc.raceEntry.entry.Entry;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EntryNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EntryNotificationService.class);

    private final JavaMailSender mailSender;
    private final BoatRepository boatRepository;
    private final PersonRepository personRepository;
    private final UserRepository userRepository;

    @Value("${spring.mail.username:noreply@raceentry.voc.com.au}")
    private String fromAddress;

    public EntryNotificationService(JavaMailSender mailSender,
                                    BoatRepository boatRepository,
                                    PersonRepository personRepository,
                                    UserRepository userRepository) {
        this.mailSender = mailSender;
        this.boatRepository = boatRepository;
        this.personRepository = personRepository;
        this.userRepository = userRepository;
    }

    public void notifyApproved(Entry entry) {
        String to = resolveOwnerEmail(entry);
        if (to == null) return;
        send(to, "Your race entry has been approved",
                "Your entry (ID: " + entry.getId() + ") has been approved. You are confirmed for the event.");
    }

    public void notifyChangesRequested(Entry entry, String comment) {
        String to = resolveOwnerEmail(entry);
        if (to == null) return;
        send(to, "Changes requested on your race entry",
                "Changes have been requested on your entry (ID: " + entry.getId() + ").\n\nComment from secretary: " + comment
                        + "\n\nPlease log in, make the necessary changes, and resubmit.");
    }

    public void notifyCancelled(Entry entry) {
        String to = resolveOwnerEmail(entry);
        if (to == null) return;
        send(to, "Your race entry has been cancelled",
                "Your entry (ID: " + entry.getId() + ") has been cancelled. Please contact the race secretary if you believe this is an error.");
    }

    private void send(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        try {
            mailSender.send(msg);
        } catch (MailException e) {
            log.warn("Failed to send notification to {}: {}", to, e.getMessage());
        }
    }

    private String resolveOwnerEmail(Entry entry) {
        return boatRepository.findById(entry.getBoatId())
                .map(Boat::getOwnerId)
                .filter(id -> id != null)
                .flatMap(personRepository::findById)
                .flatMap(person -> {
                    if (person.getEmail() != null && !person.getEmail().isBlank()) {
                        return Optional.of(person.getEmail());
                    }
                    if (person.getUserId() != null) {
                        return userRepository.findById(person.getUserId()).map(u -> u.getEmail());
                    }
                    return Optional.empty();
                })
                .orElse(null);
    }
}
