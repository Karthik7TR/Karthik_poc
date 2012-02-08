package com.thomsonreuters.uscl.ereader.mgr.web;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;

/**
 * Miscellaneous model and object fetch, handling and conversions. 
 */
public class ModelUtils {
	
	/**
	 * Returns the full name, like "John Galt", of the currently authenticated user.
	 * @return user's full name, or null if not authenticated.
	 */
	public static String getAuthenticatedUserFullName() {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		String fullName = (user != null) ? user.getFullName() : null;
		return fullName;
	}
}
