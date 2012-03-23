/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;
import com.thomsonreuters.uscl.ereader.security.Security.SecurityRole;

public class UserUtils {

	/**
	 * Returns the full name of the currently authenticated user.
	 * 
	 * @return user's full name, like "John Galt", or null if not authenticated.
	 */
	public static String getAuthenticatedUserFullName() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		return (user != null) ? user.getFullName() : null;
	}

	/**
	 * Returns the email of the currently authenticated user.
	 * 
	 * @return user's email, like "xyz@thomsonreuters.com", or null if not
	 *         authenticated.
	 */
	public static String getAuthenticatedUserEmail() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		return (user != null) ? user.getEmail() : null;
	}

	/**
	 * Returns the username of the currently authenticated user.
	 * 
	 * @return username, like U1234567, or null if not authenticated.
	 */
	public static String getAuthenticatedUserName() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		return (user != null) ? user.getUsername() : null;
	}

	/**
	 * Returns the list of roles for the currently authenticated user as a
	 * comma-separated list. Used currently for trouble-shooting presentation.
	 * 
	 * @return a CSV list of user roles
	 */
	public static String getUserRolesAsCsv() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		StringBuffer buffer = new StringBuffer();
		if (user != null) {
			Collection<GrantedAuthority> gas = user.getAuthorities();
			for (GrantedAuthority ga : gas) {
				buffer.append(ga);
				buffer.append(",");
			}
		}
		String csv = buffer.toString();
		int len = csv.length();
		if (len > 0) {
			csv = csv.substring(0, len - 1); // strip off last ","
		}
		return csv;
	}
	
	/**
	 * Returns true if the currently authenticated user can
	 * stop or restart a batch job.
	 * @param username the user who wants to stop or restart a job, may be null, which will always yield a false return value
	 */
	public static boolean isUserAuthorizedToStopOrRestartBatchJob(String username) {
		if (StringUtils.isBlank(username)) {
			return false;
		}
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		if (user != null) {
			return (isUserInRole(SecurityRole.ROLE_SUPERUSER) ||
					user.getUsername().equalsIgnoreCase(username));
		}
		return false;
	}

	/**
	 * Returns true if the currently authenticated user is in the specified role.
	 */
	public static boolean isUserInRole(SecurityRole role) {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		return (user != null) ? user.isInRole(role.toString()) : false;
	}
	
	public static boolean isUserInRole(SecurityRole[] roles) {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		
		List<String> buffer = new ArrayList<String>();
		for(SecurityRole role : roles) {
			buffer.add(role.toString());
		}
		return (user != null) ? user.isInRole(buffer.toArray(new String[buffer.size()])) : false;
	}
}
