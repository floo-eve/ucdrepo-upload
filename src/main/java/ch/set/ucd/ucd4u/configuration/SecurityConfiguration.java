package ch.set.ucd.ucd4u.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

@Configuration
@Slf4j
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Value("${ldap.url}")
    private String ldapUrls;

    @Value("${ldap.base.dn}")
    private String ldapBaseDn;

    @Value("${ldap.username}")
    private String ldapSecurityPrincipal;

    @Value("${ldap.password}")
    private String ldapPrincipalPassword;

    @Value("${ldap.search.pattern}")
    private String ldapSearchPattern;

    @Value("${ldap.enabled}")
    private String ldapEnabled;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // http.authorizeRequests().antMatchers("/login**").permitAll().antMatchers("/profile/**").fullyAuthenticated()
        // .antMatchers("/").permitAll().and().formLogin().loginPage("/login").failureUrl("/login?error")
        // .permitAll().and().logout().invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll();

        http.csrf().disable().authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin()
                .loginPage("/login").permitAll().and().logout().permitAll().and().exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        log.info("Configure Auth");

        if (Boolean.parseBoolean(ldapEnabled)) {

            auth.authenticationProvider(authenticationProvider);
            // auth.ldapAuthentication().contextSource().url(ldapUrls +
            // ldapBaseDn).managerDn(ldapSecurityPrincipal)
            // .managerPassword(ldapPrincipalPassword).and().userSearchFilter(ldapSearchPattern);

            // .passwordEncoder(new
            // org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
        } else {
            auth.inMemoryAuthentication().withUser("user").password("password").roles("USER").and().withUser("admin")
                    .password("admin").roles("ADMIN");
        }
    }

    // this method allows static resources to be neglected by spring security
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/static/**", "/webjars/**");
    }

}