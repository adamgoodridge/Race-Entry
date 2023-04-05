package au.com.voc.raceEntry.config;

import au.com.voc.raceEntry.config.ma.CustomMFAAuthenticationProvider;
import au.com.voc.raceEntry.config.ma.CustomWebAuthenticationDetailsSource;
import au.com.voc.raceEntry.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ContentSecurityPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;


@EnableWebSecurity
public class WebSecurityConfig {


    @Autowired
    private ContentSecurityPolicyHeaderWriter myHeadersWriter;
    @Autowired
    private UserService userService;
    //2FA
    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public JdbcDaoImpl userDetailsService(DataSource dataSource) {
        JdbcDaoImpl jdbcDaoImpl = new JdbcDaoImpl();
        jdbcDaoImpl.setDataSource(dataSource);
        return jdbcDaoImpl;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http/*.headers().addHeaderWriter(myHeadersWriter).and()*/

                .authorizeRequests().antMatchers("/").hasAuthority("EMPLOYEE")
                .antMatchers("/user/2mfa", "user/settings").hasAuthority("EMPLOYEE")
                .antMatchers("/boat/**").hasAuthority("EMPLOYEE")
                .antMatchers("/driver/**").hasAuthority("EMPLOYEE")
                .antMatchers("/entry/list").hasAuthority("ADMIN")
                .antMatchers("/entry/add/**").hasAuthority("EMPLOYEE")
                .antMatchers("/entry/processForm/**").hasAuthority("EMPLOYEE")
                .antMatchers("/event/list").hasAuthority("EMPLOYEE")
                .antMatchers("/event/**", "/entry/entriesByEvent/**").hasAuthority("ADMIN")
                .and().logout().permitAll()
                .and().exceptionHandling().accessDeniedPage("/access-denied");
        http.formLogin(form -> form.loginPage("/login")
                //2FA
                .authenticationDetailsSource(authenticationDetailsSource)
                .permitAll());
        http.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).invalidateHttpSession(true).clearAuthentication(true).logoutSuccessUrl("/login?logout")
        );
        //http.headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable();
        return http.build();
    }

    //https://stackoverflow.com/questions/49420914/thymeleaf-intellij-and-spring-boot-not-loading-css-files-correctly
    private static final String[] PUBLIC_MATCHERS = {
            "/css/**",
            "/js/**",
            "/webjars/**",
            "/static/**"
    };

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        ///DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        CustomMFAAuthenticationProvider auth = new CustomMFAAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

}