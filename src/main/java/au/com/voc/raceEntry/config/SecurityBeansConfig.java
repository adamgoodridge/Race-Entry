package au.com.voc.raceEntry.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;

@Configuration
public class SecurityBeansConfig {
    private static final String DEFAULT_SRC_SELF_POLICY = "default-src 'self';" +
            "script-src http://localhost:8080 https://cdn.jsdelivr.net; " +
            "style-src 'self'; frame-ancestors 'none';" +
            "Set-Cookie: SameSite:strict;" +
            "img-src 'self' https://chart.googleapis.com;" +
            "style-src-elem 'self' https://cdn.jsdelivr.net https://maxcdn.bootstrapcdn.com https://getbootstrap.com;  " +
            "form-action 'self'";

    //private static final String DEFAULT_SRC_SELF_POLICY = "default-src 'self'; style-src: 'none' script-src: 'self'; frame-ancestors 'self'; Set-Cookie: SameSite:lax";
//https://stackoverflow.com/questions/59699927/getting-neither-bindingresult-nor-plain-target-object-for-bean-name-bean-name
    //To use over classes
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ContentSecurityPolicyHeaderWriter myWriter(@Value("${#my.policy.directive:DEFAULT_SRC_SELF_POLICY}") String initalDirectives) {
        return new ContentSecurityPolicyHeaderWriter(DEFAULT_SRC_SELF_POLICY);
    }
    //authProvide bean in WebSecurityBean
}
