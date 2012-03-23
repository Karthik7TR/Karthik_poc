package com.thomsonreuters.uscl.ereader.security;

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

}
