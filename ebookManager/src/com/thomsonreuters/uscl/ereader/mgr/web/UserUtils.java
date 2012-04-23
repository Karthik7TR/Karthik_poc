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
import org.apache.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;

import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;

public class UserUtils {
	private static final Logger log = Logger.getLogger(UserUtils.class);
	
	public enum SecurityRole { ROLE_SUPERUSER, ROLE_PUBLISHER_PLUS, ROLE_PUBLISHER, ROLE_SUPPORT, ROLE_GUEST, ROLE_EDITOR };

	/**
	 * Returns the full name of the currently authenticated user.
	 * 
	 * @return user's full name, like "John Galt", or null if not authenticated.
	 */
	public static String getAuthenticatedUserFullName() {
		CobaltUser user = CobaltUser.getAuthenticatedUser();
		return (user != null) ? user.getFullName() : null;
	}

	/**
	 * Returns the email of the currently authenticated user.
	 * 
	 * @return user's email, like "xyz@thomsonreuters.com", or null if not
	 *         authenticated.
	 */
	public static String getAuthenticatedUserEmail() {
		CobaltUser user = CobaltUser.getAuthenticatedUser();
		return (user != null) ? user.getEmail() : null;
	}

	/**
	 * Returns the username of the currently authenticated user.
	 * 
	 * @return username, like U1234567, or null if not authenticated.
	 */
	public static String getAuthenticatedUserName() {
		CobaltUser user = CobaltUser.getAuthenticatedUser();
		return (user != null) ? user.getUsername() : null;
	}

	/**
	 * Returns the list of roles for the currently authenticated user as a
	 * comma-separated list. Used currently for trouble-shooting presentation.
	 * 
	 * @return a CSV list of user roles
	 */
	public static String getUserRolesAsCsv() {
		CobaltUser user = CobaltUser.getAuthenticatedUser();
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
	 * Returns true if the currently authenticated user is in the specified role.
	 */
	public static boolean isUserInRole(SecurityRole role) {
		CobaltUser user = CobaltUser.getAuthenticatedUser();
		boolean isInRole = false;
		if (user != null) {
			isInRole = user.isInRole(role.toString());
		}
			return isInRole;
	}
	
	public static boolean isUserInRole(SecurityRole[] roles) {
		CobaltUser user = CobaltUser.getAuthenticatedUser();
		
		List<String> buffer = new ArrayList<String>();
		for(SecurityRole role : roles) {
			buffer.add(role.toString());
		}
		return (user != null) ? user.isInRole(buffer.toArray(new String[buffer.size()])) : false;
	}
	
	/**
	 * Returns true if the currently authenticated user can
	 * stop or restart a batch job.
	 * @param usernameThatStartedTheJob the user who wants to stop or restart a job, may be null
	 */
	public static boolean isUserAuthorizedToStopOrRestartBatchJob(String usernameThatStartedTheJob) {
//		log.debug("Username that started the job: " + usernameThatStartedTheJob);
		CobaltUser user = CobaltUser.getAuthenticatedUser();
//		log.debug("Current authenticated user: " + user);
		if (user == null) { // if not authenticated
//			log.debug("Null user - not authenticated.");
			return false;
		}
		if (user.isInRole(SecurityRole.ROLE_SUPERUSER.toString())) {  // if they are a superuser
//			log.debug("Current user is a superuser - proceed.");
			return true;
		}
		if (StringUtils.isBlank(usernameThatStartedTheJob)) {
//			log.warn("Username for user that started the job is blank.");
			return false;
		}
		if (user.getUsername().equalsIgnoreCase(usernameThatStartedTheJob)) {
//			log.debug("Authenticated username matches user that started the job.");
			return true;
		}
		return false;
	}
}
