//demo only
package au.com.voc.raceEntry.user.Encryt;

import au.com.voc.raceEntry.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class Convertor {

    private static final Logger log = LoggerFactory.getLogger(Convertor.class);

    @Autowired
    static UserService userService;

    public static void main(String[] args) {
        log.debug("{}", userService.loadUserByUsername("w").getUsername());
    }
}
