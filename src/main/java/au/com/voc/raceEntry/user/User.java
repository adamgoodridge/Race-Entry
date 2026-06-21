package au.com.voc.raceEntry.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private String role;

    @Setter
    @Column(name = "person_id")
    private Long personId;

    protected User() {}

    public User(String username, String password, String role, Long personId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.personId = personId;
    }
}
