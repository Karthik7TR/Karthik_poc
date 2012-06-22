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
	private static String environmentName;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getPrincipal().toString();
		String password = (String) authentication.getCredentials();
		
		if (mapGroupFromUsername(username) == null) {
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
	
	public static SecurityRole mapGroupFromUsername(String name) {
		// Only allow this to work in Test, CI, and Workstation for testing purposes.
		if(environmentName != null && (environmentName.equalsIgnoreCase("ci") ||
				environmentName.equalsIgnoreCase("test") ||
				environmentName.equalsIgnoreCase("workstation"))) {
			if (name.contains(SecurityRole.ROLE_GUEST.toString())) {
				return SecurityRole.ROLE_GUEST;
			}
			if (name.contains(SecurityRole.ROLE_PUBLISHER_PLUS.toString())) {
				return SecurityRole.ROLE_PUBLISHER_PLUS;
			}
			if (name.contains(SecurityRole.ROLE_PUBLISHER.toString())) {
				return SecurityRole.ROLE_PUBLISHER;
			}
			if (name.contains(SecurityRole.ROLE_SUPERUSER.toString())) {
				return SecurityRole.ROLE_SUPERUSER;
			}
			if (name.contains(SecurityRole.ROLE_SUPPORT.toString())) {
				return SecurityRole.ROLE_SUPPORT;
			}
			if (name.contains(SecurityRole.ROLE_EDITOR.toString())) {
				return SecurityRole.ROLE_EDITOR;
			}
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> paramClass) {
		return true;
	}
	
	@Required
	public void setUserDetailsService(UserDetailsService service) {
		this.userDetailsService = service;
	}
	@Required
	public static void setEnvironmentName(String environment) {
		environmentName = environment;
	}
}
