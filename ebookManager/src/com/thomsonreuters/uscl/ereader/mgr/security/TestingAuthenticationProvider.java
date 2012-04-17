package com.thomsonreuters.uscl.ereader.mgr.security;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils.SecurityRole;

/**
 * Will authenticate a username of any valid application role name along with a password of "password".
 * Example: ROLE_PUBLISHER / password will successfully authenticate.
 */
public class TestingAuthenticationProvider implements AuthenticationProvider {
	//private static final Logger log = Logger.getLogger(TestingAuthenticationProvider.class);
	
	private UserDetailsService userDetailsService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getPrincipal().toString();
		String password = (String) authentication.getCredentials();
		try {  // Verify the username is one of the valid role names defined for the application
			SecurityRole.valueOf(username);
		} catch (IllegalArgumentException e) {
			return null;
		}
		if (!"password".equals(password)) {	// Verify the password is "password"
			return null;
		}
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails, password, userDetails.getAuthorities());
		return authToken;
	}

	@Override
	public boolean supports(Class<?> paramClass) {
		return true;
	}
	
	@Required
	public void setUserDetailsService(UserDetailsService service) {
		this.userDetailsService = service;
	}
}
