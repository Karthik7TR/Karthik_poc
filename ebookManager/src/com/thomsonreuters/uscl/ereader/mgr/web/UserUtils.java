/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;

public class UserUtils {
	
	/**
	 * Returns the full name, like "John Galt", of the currently authenticated
	 * user.
	 * 
	 * @return user's full name, or null if not authenticated.
	 */
	public static String getAuthenticatedUserFullName() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		String fullName = (user != null) ? user.getFullName() : null;
		return fullName;
	}
	
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
		if (csv.length() > 0) {
			csv = csv.substring(0, csv.length()-1);  // strip off last ","
		}
		return csv;
	}

	/**
	 * Checks if the user has super user role
	 * 
	 * @return
	 */
	public static boolean isSuperUser() {
		boolean superUser = false;

		LdapUserInfo ldapUserInfo = LdapUserInfo.getAuthenticatedUser();

		if (ldapUserInfo != null) {
			Collection<GrantedAuthority> authorities = ldapUserInfo
					.getAuthorities();

			if (authorities != null) {
				for (GrantedAuthority authority : authorities) {
					if (WebConstants.KEY_GENERATE_BUTTON_ROLE.equals(authority
							.getAuthority())) {
						superUser = true;
						break;
					}
				}
			}
		}
		return superUser;
	}
}
