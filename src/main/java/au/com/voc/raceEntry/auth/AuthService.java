package au.com.voc.raceEntry.auth;

import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.security.JwtUtil;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PersonRepository personRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ConflictException("Username already taken: " + username);
        }
        Person person = personRepository.save(new Person(username, "", username + "@placeholder.local"));
        String encoded = passwordEncoder.encode(rawPassword);
        userRepository.save(new User(username, encoded, "ROLE_USER", person.getId()));
        return jwtUtil.generateToken(username, "ROLE_USER", person.getId());
    }

    public String login(String username, String rawPassword) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, rawPassword));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Bad credentials");
        }
        User user = userRepository.findByUsername(username).orElse(null);
        String role = user != null ? user.getRole() : "ROLE_USER";
        Long personId = user != null ? user.getPersonId() : null;
        return jwtUtil.generateToken(username, role, personId);
    }
}
