package com.thomsonreuters.uscl.ereader.mgr.security;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * User info object populated from the LDAP directory server.
 */
public class CobaltUser extends User {
	
	private static final long serialVersionUID = -5170583643786179311L;
	private static final String UNUSED_PASSWORD = "<secret>";
	private String firstName;
	private String lastName;
	private String email;

	public CobaltUser(String username,
					  String firstName, String lastName, String email,
					  Collection<GrantedAuthority> authorities) {
		super(username, UNUSED_PASSWORD , true, true, true, true, authorities);
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	/**
	 * Returns the currently authenticated CodesUser object or null if not user is authenticated.
	 * Assumes usage within a Spring Security based web application.
	 * @return currently authenticated user object, or null if not authenticated.
	 */
	public static CobaltUser getAuthenticatedUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
        	return null;
        }
        Object principal = authentication.getPrincipal();
        CobaltUser user = (principal instanceof CobaltUser) ? (CobaltUser) authentication.getPrincipal() : null;
        return user;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getFullName() {
		StringBuilder fullName = new StringBuilder();
		if (StringUtils.isNotBlank(firstName)) {
			fullName.append(firstName);
			fullName.append(" ");
		}
		if (StringUtils.isNotBlank(lastName)) {
			fullName.append(lastName);
		}
		return fullName.toString();
	}
	public String getEmail() {
		return email;
	}
    /**
     * Returns true if user is in the specified role.
     * @param role the granted authority to check
     * @return true if user is in role, false otherwise.
     */
	public boolean isInRole(String role) {
		return isInRole(new SimpleGrantedAuthority(role));
	}
    private boolean isInRole(GrantedAuthority checkAuthority) {
    	Collection<GrantedAuthority> authorities = this.getAuthorities();
    	for (GrantedAuthority authority : authorities) {
    		if (authority.equals(checkAuthority)) {
    			return true;
    		}
    	}
        return false;
    }

    /**
     * Returns true if the user is in any of the listed roles.
     */
    public boolean isInRole(String[] roles) {
    	for (String role : roles) {
            if (isInRole(role)) {
                return true;
            }
        }
        return false;
    }
	public String toString() {
		return "username="+getUsername()+", firstName="+getFirstName()+", lastName="+getLastName()+
			   ", email="+getEmail()+", authorities="+rolesToString(getAuthorities());
	}
	public static String rolesToString(Collection<GrantedAuthority> authorities) {
		StringBuilder roles = new StringBuilder("[");
		int i = 0;
		for (GrantedAuthority authority : authorities) {
			roles.append(authority);
			if (i+1 < authorities.size()) {
				roles.append(",");
			}
			i++;
		}
		roles.append("]");
		return roles.toString();
	}
}
