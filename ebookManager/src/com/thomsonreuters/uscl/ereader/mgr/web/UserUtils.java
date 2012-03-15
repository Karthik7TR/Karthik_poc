/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;
import com.thomsonreuters.uscl.ereader.Security.SecurityRole;

public class UserUtils {
	
	/**
	 * Returns the full name of the currently authenticated user.
	 * @return user's full name, like "John Galt", or null if not authenticated.
	 */
	public static String getAuthenticatedUserFullName() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		return (user != null) ? user.getFullName() : null;
	}
	
	/**
	 * Returns the full name of the currently authenticated user.
	 * @return user's email, like "John Galt", or null if not authenticated.
	 */
	public static String getAuthenticatedUserEmail() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		return (user != null) ? user.getEmail() : null;
	}

	/**
	 * Returns the list of roles for the currently authenticated user as a comma-separated
	 * list.  Used currently for trouble-shooting presentation.
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
			csv = csv.substring(0, len-1);  // strip off last ","
		}
		return csv;
	}

	/**
	 * Returns true if the currently authenticated user is an application super-user.
	 */
	public static boolean isSuperUser() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		return (user != null) ? user.isInRole(SecurityRole.ROLE_SUPERUSER.toString()) : false;
	}
}
