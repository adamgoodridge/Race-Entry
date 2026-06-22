package au.com.voc.raceEntry.auth;

import au.com.voc.raceEntry.exception.ConflictException;
import au.com.voc.raceEntry.person.Person;
import au.com.voc.raceEntry.person.PersonRepository;
import au.com.voc.raceEntry.security.JwtUtil;
import au.com.voc.raceEntry.user.User;
import au.com.voc.raceEntry.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PersonRepository personRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtUtil jwtUtil;
    @InjectMocks AuthService authService;

    @Test
    void register_success_returnsToken() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken("alice", "ROLE_USER")).thenReturn("jwt-token");

        String token = authService.register("alice", "secret");

        assertThat(token).isEqualTo("jwt-token");
        verify(personRepository).save(any(Person.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsConflictException() {
        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(new User("alice", "hashed", "ROLE_USER", null)));

        assertThatThrownBy(() -> authService.register("alice", "secret"))
                .isInstanceOf(ConflictException.class);

        verify(userRepository, never()).save(any());
        verify(personRepository, never()).save(any());
    }

    @Test
    void login_success_returnsToken() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(new User("alice", "hashed", "ROLE_USER", null)));
        when(jwtUtil.generateToken("alice", "ROLE_USER")).thenReturn("jwt-token");

        String token = authService.login("alice", "secret");

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void login_badPassword_throwsUnauthorized() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login("alice", "wrong"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatus())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
