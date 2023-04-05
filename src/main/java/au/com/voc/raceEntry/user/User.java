//demo only
package au.com.voc.raceEntry.user;

import au.com.voc.raceEntry.user.register.Role;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Table(name = "users")
@Entity
public class User {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "user_id")
    private Long id;
    @ColumnTransformer(
            read = "AES_DECRYPT(username, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')",
            write = "AES_ENCRYPT(?, '[ENCRYPTION_KEY_GOES_HERE_REMOVED_FOR_GITHUB]')")
    @Column(name = "username")
    private String username;
    @Basic
    @Column(name = "password")
    private String password;

    @Basic
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "reset_datetime")
    private Date resetDate;
    @Column(name = "reset_code")
    private String resetCode;
    @Column(name = "enabled")
    private boolean isEnabled;
    @Column(name = "using2fa")
    private boolean isUsing2Fa;

    @Column(name = "secret")
    private String secret;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private Collection<Role> roles;

    public User() {
        super();
    }

    public boolean isUsing2Fa() {
        return isUsing2Fa;
    }

    public void setIsUsing2Fa(boolean isUsing2Fa) {
        this.isUsing2Fa = isUsing2Fa;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public boolean isAdmin() {
        return roles.stream().anyMatch(r -> r.getName().equals("ADMIN"));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Date getResetDate() {
        return resetDate;
    }

    public void setResetDate(Date resetDate) {
        this.resetDate = resetDate;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", enabled=" + isEnabled +
                ", firstName='" + firstName + '\'' +
                ", resetDate=" + resetDate +
                ", resetCode='" + resetCode + '\'' +
                ", isUsing2FA=" + isUsing2Fa +
                ", secret='" + secret + '\'' +
                ", roles=" + roles +
                '}';
    }
}