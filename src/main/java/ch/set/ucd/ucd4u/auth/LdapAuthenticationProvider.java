package ch.set.ucd.ucd4u.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ch.set.ucd.ucd4u.model.User;

import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LdapAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${ldap.group.search.dn}")
    private String groupDn;

    @Value("${ldap.base.dn}")
    private String ldapBaseDn;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String userId = authentication.getName();
        final Object credentials = authentication.getCredentials();
        log.info("credentials class: " + credentials.getClass());
        if (!(credentials instanceof String)) {
            return null;
        }

        final String password = credentials.toString();

        log.debug("User to authenticate: " + userId);

        final Filter filter = new EqualsFilter("uid", userId);
        final boolean authenticated = ldapTemplate.authenticate("", filter.toString(), password);

        log.debug("User is authenticated: " + authenticated);

        if (authenticated) {

            AndFilter filterDn = new AndFilter();
            filterDn.and(new EqualsFilter("objectclass", "OpenLDAPperson")).and(new EqualsFilter("cn", userId));

            // Get full dn of User
            List<User> users = ldapTemplate.search("", filterDn.toString(), new DnMapper());

            if (users.size() != 1) {
                throw new UsernameNotFoundException("User not unique");
            }

            User authenticatedUser = users.get(0);

            authenticatedUser.setFulldn(authenticatedUser.getDn() + "," + ldapBaseDn);

            log.debug("got dn: " + authenticatedUser.getFulldn());

            AndFilter filterAnd = new AndFilter();
            filterAnd.and(new EqualsFilter("objectclass", "groupOfUniqueNames"))
                    .and(new EqualsFilter("uniqueMember", authenticatedUser.getFulldn()));

            log.debug("filer: " + filterAnd.toString());

            List<String> groups = ldapTemplate.search(groupDn, filterAnd.toString(), new GroupContextMapper());

            ArrayList<LdapAuthority> listAuthorities = new ArrayList<LdapAuthority>();

            if (groups.stream().anyMatch(str -> str.equals("ucd4u-admin"))) {
                listAuthorities.add(new LdapAuthority("ROLE_ADMIN", authenticatedUser.getFulldn()));
                authenticatedUser.addRole("ROLE_ADMIN");
            } else if (groups.stream().anyMatch(str -> str.equals("ucd4u-user"))) {
                listAuthorities.add(new LdapAuthority("ROLE_USER", authenticatedUser.getFulldn()));
                authenticatedUser.addRole("ROLE_USER");
            }

            if (authenticatedUser.getRoles().size() == 0) {
                log.debug("no matching role for user " + userId);
                throw new InsufficientAuthenticationException("No roles for user");
            }

            log.debug("granted Roles: "
                    + listAuthorities.stream().map(Object::toString).collect(Collectors.joining(",")));

            // if (log.isDebugEnabled()) {

            // for (String group : groups) {
            // log.debug(group);
            // }
            // }

            return new UsernamePasswordAuthenticationToken(authenticatedUser, password, listAuthorities);

        }

        return null;

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * LDAPTemplate mapper for getting group names.
     *
     * @author scole
     *
     */
    static class GroupContextMapper implements ContextMapper<String> {
        @Override
        public String mapFromContext(Object ctx) {
            DirContextAdapter context = (DirContextAdapter) ctx;
            String group = context.getStringAttribute("cn");
            return group;
        }

    }

    static class DnMapper implements ContextMapper<User> {
        @Override
        public User mapFromContext(Object ctx) throws NamingException {
            if (ctx == null) {
                return null;
            }

            DirContextAdapter context = (DirContextAdapter) ctx;
            return new User(context.getDn().toString(), context.getStringAttribute("cn"),
                    context.getStringAttribute("sn"));

        }
    }

}