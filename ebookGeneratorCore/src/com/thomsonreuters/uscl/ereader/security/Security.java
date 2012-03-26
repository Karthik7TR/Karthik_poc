package com.thomsonreuters.uscl.ereader.security;

import org.apache.commons.lang.StringUtils;

import com.thomsonreuters.codes.security.authentication.LdapUserInfo;

public class Security {
	
	/**
	 * Valid security roles for the application, as mapped from the groups.
	 * 1) ROLE_SUPERUSER (most privileged)
	 * 2) ROLE_PUBLISHER_PLUS
	 * 3) ROLE_PUBLISHER
	 * 4) ROLE_SUPPORT
	 * 5) ROLE_GUEST  (least privileged)
	 */
	public enum SecurityRole { ROLE_SUPERUSER, ROLE_PUBLISHER_PLUS, ROLE_PUBLISHER, ROLE_SUPPORT, ROLE_GUEST };
	
	/**
	 * Returns true if the currently authenticated user can
	 * stop or restart a batch job.
	 * @param usernameThatStartedTheJob the user who wants to stop or restart a job, may be null
	 */
	public static boolean isUserAuthorizedToStopOrRestartBatchJob(String usernameThatStartedTheJob) {
		LdapUserInfo user = LdapUserInfo.getAuthenticatedUser();
		if (user == null) { // if not authenticated
			return false;
		}
		if (user.isInRole(SecurityRole.ROLE_SUPERUSER.toString())) {  // if they are a superuser
			return true;
		}
		if (StringUtils.isBlank(usernameThatStartedTheJob)) {
			return false;
		}
		return (user.getUsername().equalsIgnoreCase(usernameThatStartedTheJob));
	}

}
