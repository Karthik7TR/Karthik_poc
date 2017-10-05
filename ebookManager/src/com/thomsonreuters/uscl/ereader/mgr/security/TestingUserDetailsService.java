package com.thomsonreuters.uscl.ereader.mgr.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TestingUserDetailsService implements UserDetailsService {
    //private static Logger log = LogManager.getLogger(TestingUserDetailsService.class);
    /**
     * User details where the username is also the role name that will be assigned.
     * E.g. log in as "ROLE_SUPERUSER" and be put into that role.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        //log.debug("username="+username);
        final Collection<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(TestingAuthenticationProvider.mapGroupFromUsername(username).toString()));

        final UserDetails user = new CobaltUser(username, "User", username, username + "@bogus.tr.com", roles);
        return user;
    }
}
