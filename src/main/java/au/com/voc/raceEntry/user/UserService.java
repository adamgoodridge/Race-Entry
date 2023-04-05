//demo only
package au.com.voc.raceEntry.user;

import au.com.voc.raceEntry.user.register.CrmResetPasswordRequest;
import au.com.voc.raceEntry.user.register.CrmUser;
import au.com.voc.raceEntry.user.register.Role;
import au.com.voc.raceEntry.user.register.RoleRepository;
import au.com.voc.raceEntry.utils.NotLoggedInException;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    private final JavaMailSender emailSender;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final char[] CHARAS = (ALPHABET + ALPHABET.toUpperCase(Locale.ROOT) + "()!@$-_=-").toCharArray();

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder, JavaMailSender emailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    private void sendRecoveryEmail(User user) {
        String subject = "Boat Entry Password Recovery";
        StringBuilder sb = new StringBuilder();
        sb.append("Hi ");
        sb.append(user.getFirstName());
        sb.append(",\n\nYou are receiving this email as you recently requested to reset your password, if it was you, click the link here: \n\n");
        String link = "http://localhost:8084/user/recovery/" + user.getId() + "/" + user.getResetCode();
        sb.append(link);
        sb.append("\n\nThis will expired in 4 hours from ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getResetDate());
        calendar.add(Calendar.HOUR, -4);
        sb.append(calendar.getTime());
        sb.append("\n\nIf you didn't request this, please ignore this email.");

        sb.append("\n\n Kind Regards,\n The Boat Entry Team");
        sendEmail(user.getUsername(), subject, sb.toString());
    }

    private void sendEmail(
            String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("adam_goodridge1996@hotmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByUserId(Long userId) {
        return userRepository.getReferenceById(userId);
    }

    public User getCurrentUser() throws NotLoggedInException {
        try {
            Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
            return (User) curAuth.getPrincipal();
        } catch (ClassCastException e) {
            throw new NotLoggedInException(e);
        }
    }

    public void save(CrmUser crmUser) {
        User user = new User();
        user.setUsername(crmUser.getEmail());
        user.setPassword(passwordEncoder.encode(crmUser.getPassword()));
        user.setFirstName(crmUser.getFirstName());
        Role role = roleRepository.findByRoleName("EMPLOYEE");
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);
    }

    public void updatePassword(User user) {
        System.out.println(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setResetCode(null);
        user.setResetDate(null);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    public void sendPasswordRecovery(CrmResetPasswordRequest crmResetPasswordRequest) {
        User user = userRepository.findByEmail(crmResetPasswordRequest.getEmail());
        if (user != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR, 4);
            user.setResetDate(calendar.getTime());
            user.setResetCode(generateRandomString());
            sendRecoveryEmail(user);
            userRepository.save(user);
        }
    }

    private String generateRandomString() {
        java.util.Random random = new java.util.Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 60; i++) {
            int index = random.nextInt(CHARAS.length - 1);
            sb.append(CHARAS[index]);
        }
        return sb.toString();
    }


    public static final String QR_PREFIX =
            "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    private static final String APP_NAME = "Boat Entry";

    public String generateQRUrl(User user) {
        return QR_PREFIX + URLEncoder.encode(String.format(
                        "otpauth://totp/%s:%s?secret=%s",
                        APP_NAME, user.getUsername(), user.getSecret()),
                StandardCharsets.UTF_8);
        //&issuer=%s
    }

    public User generateUser2FA() {
        Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) curAuth.getPrincipal();
        currentUser.setSecret(Base32.random());
        currentUser = userRepository.save(currentUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                currentUser, currentUser.getPassword(), curAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return currentUser;
    }

    public void confirm2Fa() {
        Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) curAuth.getPrincipal();
        currentUser.setIsUsing2Fa(true);
        currentUser = userRepository.save(currentUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                currentUser, currentUser.getPassword(), curAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    //user want an iq code and now chosen and didn't confirm it
    public void cancel2Fa() {
        Authentication curAuth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) curAuth.getPrincipal();
        currentUser.setIsUsing2Fa(false);
        currentUser.setSecret(null);
        currentUser = userRepository.save(currentUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                currentUser, currentUser.getPassword(), curAuth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
