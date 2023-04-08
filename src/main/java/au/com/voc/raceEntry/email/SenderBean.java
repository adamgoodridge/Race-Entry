//demo only
package au.com.voc.raceEntry.email;

import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

public class SenderBean {
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        //mailSender.setHost("smtp.gmail.com");
        mailSender.setHost("smtp-mail.outlook.com");
        mailSender.setPort(587);

        mailSender.setUsername("remove ");
        mailSender.setPassword("remove ");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
