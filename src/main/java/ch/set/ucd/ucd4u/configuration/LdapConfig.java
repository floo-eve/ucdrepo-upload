package ch.set.ucd.ucd4u.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class LdapConfig {

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.base.dn}")
    private String ldapBaseDn;

    @Value("${ldap.username}")
    private String ldapSecurityPrincipal;

    @Value("${ldap.password}")
    private String ldapPrincipalPassword;

    @Value("${ldap.search.pattern}")
    private String ldapSearchPattern;

    @Bean
    public LdapContextSource contextSource() {
        log.debug("------------- ldapTemplate return");

        final LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBaseDn);
        contextSource.setUserDn(ldapSecurityPrincipal);
        contextSource.setPassword(ldapPrincipalPassword);
        contextSource.afterPropertiesSet();
        contextSource.setAuthenticationStrategy(new SimpleDirContextAuthenticationStrategy());
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        log.debug("------------- ldapTemplate return");
        return new LdapTemplate(contextSource());
    }

}