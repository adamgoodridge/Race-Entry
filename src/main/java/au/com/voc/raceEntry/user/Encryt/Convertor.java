//demo only
package au.com.voc.raceEntry.user.Encryt;

import au.com.voc.raceEntry.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class Convertor {
    @Autowired
    static UserService userService;

    public static void main(String[] args) {
        System.out.println(userService.loadUserByUsername("w").getUsername());
    }
}
