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
	 * Checks if the user has super user role
	 * 
	 * @return
	 */
	public static boolean isSuperUser() {
		boolean superUser = false;

		LdapUserInfo ldapUserInfo = ModelUtils.getAuthenticatedLDapUserInfo();

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
