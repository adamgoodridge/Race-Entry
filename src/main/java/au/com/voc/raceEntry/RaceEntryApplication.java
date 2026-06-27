//demo only
package au.com.voc.raceEntry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RaceEntryApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaceEntryApplication.class, args);
    }

}
