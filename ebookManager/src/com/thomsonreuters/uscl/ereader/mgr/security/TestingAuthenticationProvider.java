package com.thomsonreuters.uscl.ereader.mgr.security;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils.SecurityRole;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Will authenticate a username of any valid application role name along with a password of "password".
 * Example: ROLE_PUBLISHER / password will successfully authenticate.
 */
public class TestingAuthenticationProvider implements AuthenticationProvider
{
    //private static final Logger log = LogManager.getLogger(TestingAuthenticationProvider.class);

    private UserDetailsService userDetailsService;
    private static String environmentName;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException
    {
        final String username = authentication.getPrincipal().toString();
        final String password = (String) authentication.getCredentials();

        if (mapGroupFromUsername(username) == null)
        {
            return null;
        }
        if (!"password".equals(password))
        { // Verify the password is "password"
            return null;
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        return authToken;
    }

    public static SecurityRole mapGroupFromUsername(final String name)
    {
        // Only allow this to work in Test, CI, and Workstation for testing purposes.
        if (environmentName != null
            && (environmentName.equalsIgnoreCase("ci")
                || environmentName.equalsIgnoreCase("cicontent")
                || environmentName.equalsIgnoreCase("test")
                || environmentName.equalsIgnoreCase("testcontent")
                || environmentName.equalsIgnoreCase("workstation")))
        {
            if (name.contains(SecurityRole.ROLE_GUEST.toString()))
            {
                return SecurityRole.ROLE_GUEST;
            }
            if (name.contains(SecurityRole.ROLE_PUBLISHER_PLUS.toString()))
            {
                return SecurityRole.ROLE_PUBLISHER_PLUS;
            }
            if (name.contains(SecurityRole.ROLE_PUBLISHER.toString()))
            {
                return SecurityRole.ROLE_PUBLISHER;
            }
            if (name.contains(SecurityRole.ROLE_SUPERUSER.toString()))
            {
                return SecurityRole.ROLE_SUPERUSER;
            }
            if (name.contains(SecurityRole.ROLE_SUPPORT.toString()))
            {
                return SecurityRole.ROLE_SUPPORT;
            }
            if (name.contains(SecurityRole.ROLE_EDITOR.toString()))
            {
                return SecurityRole.ROLE_EDITOR;
            }
        }
        return null;
    }

    @Override
    public boolean supports(final Class<?> paramClass)
    {
        return true;
    }

    @Required
    public void setUserDetailsService(final UserDetailsService service)
    {
        userDetailsService = service;
    }

    @Required
    public static void setEnvironmentName(final String environment)
    {
        environmentName = environment;
    }
}
