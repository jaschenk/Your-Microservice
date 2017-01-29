package your.microservice.core.configuration;

import your.microservice.core.system.messaging.jms.MessagePublisherService;
import your.microservice.core.security.idp.security.AuthenticationTokenFilter;
import your.microservice.core.security.idp.security.EntryPointUnauthorizedHandler;
import your.microservice.core.security.idp.security.YourMicroserviceSecurityConstants;
import your.microservice.core.security.idp.security.service.SecurityService;
import your.microservice.core.security.idp.security.YourMSAuthenticationManager;
import your.microservice.core.security.idp.security.YourMicroserviceUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * IdPSecurityConfiguration
 */
@Configuration
@Order(3)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class IdPSecurityConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    private EntryPointUnauthorizedHandler unauthorizedHandler;

    @Autowired
    private SecurityService securityService;

    /**
     * From Previous Security Model.
     */
    @Autowired
    private YourMicroserviceUserDetailsService detailsService;

    /**
     * MessagePublisherService
     */
    @Autowired
    private MessagePublisherService messagePublisherService;

    /**
     * Provides accessing the current user within Spring Data queries using SpEL.
     *
     * @return SecurityEvaluationContextExtension
     */
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }


    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(detailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(YourMicroserviceSecurityConstants.BCRYPT_STRENGTH_SETTING);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        //return super.authenticationManagerBean();
        return new YourMSAuthenticationManager(detailsService, messagePublisherService);
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationTokenFilter;
    }

    @Bean
    public SecurityService securityService() {
        return this.securityService;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // @formatter:off
        httpSecurity
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(this.unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Auth
                .antMatchers("/api/auth").permitAll()

                // Special Swagger Endpoint for Auth
                .antMatchers("/api/auth/").permitAll()

                // Swagger UI
                .antMatchers("/api/docs").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/webjars/**").permitAll()

                // Allow Landing Pages
                .antMatchers("/").permitAll()
                .antMatchers("/s").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/portal/**").permitAll()

                // Allow UI Resources
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/html/**").permitAll()

                // Allow Api App Information Base Head and Registration Processing
                .antMatchers("/api/**/v*").permitAll()

                .antMatchers("/api/entity/v*/resetPassword/*").permitAll()
                .antMatchers("/api/entity/v*/activateAccount/**").permitAll()

                // All Other Request Authenticated.
                .anyRequest().authenticated();

        // @formatter:on

        // add this line to use H2 web console
        httpSecurity.headers().frameOptions().disable();

        // Custom JWT based authentication
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

}
