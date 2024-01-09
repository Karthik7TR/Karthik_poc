package com.thomsonreuters.uscl.ereader.mgr.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

public class UserUtils {
    public enum SecurityRole {
        ROLE_SUPERUSER,
        ROLE_PUBLISHER_PLUS,
        ROLE_PUBLISHER,
        ROLE_SUPPORT,
        ROLE_GUEST,
        ROLE_EDITOR
    };

    /**
     * Returns the full name of the currently authenticated user.
     *
     * @return user's full name, like "John Galt", or null if not authenticated.
     */
    public static String getAuthenticatedUserFullName() {
        final CobaltUser user = CobaltUser.getAuthenticatedUser();
        return (user != null) ? user.getFullName() : null;
    }

    /**
     * Returns the email of the currently authenticated user.
     *
     * @return user's email, like "xyz@thomsonreuters.com", or null if not
     *         authenticated.
     */
    public static String getAuthenticatedUserEmail() {
        final CobaltUser user = CobaltUser.getAuthenticatedUser();
        return (user != null) ? user.getEmail() : null;
    }

    /**
     * Returns the username of the currently authenticated user.
     *
     * @return username, like U1234567, or null if not authenticated.
     */
    public static String getAuthenticatedUserName() {
        final CobaltUser user = CobaltUser.getAuthenticatedUser();
        return (user != null) ? user.getUsername() : null;
    }

    /**
     * Returns the list of roles for the currently authenticated user as a
     * comma-separated list. Used currently for trouble-shooting presentation.
     *
     * @return a CSV list of user roles
     */
    public static String getUserRolesAsCsv() {
        final CobaltUser user = CobaltUser.getAuthenticatedUser();
        final StringBuffer buffer = new StringBuffer();
        if (user != null) {
            final Collection<GrantedAuthority> gas = user.getAuthorities();
            for (final GrantedAuthority ga : gas) {
                buffer.append(ga);
                buffer.append(",");
            }
        }
        String csv = buffer.toString();
        final int len = csv.length();
        if (len > 0) {
            csv = csv.substring(0, len - 1); // strip off last ","
        }
        return csv;
    }

    /**
     * Returns true if the currently authenticated user is in the specified role.
     */
    public static boolean isUserInRole(final SecurityRole role) {
        final CobaltUser user = CobaltUser.getAuthenticatedUser();
        boolean isInRole = false;
        if (user != null) {
            isInRole = user.isInRole(role.toString());
        }
        return isInRole;
    }

    public static boolean isUserInRole(final SecurityRole[] roles) {
        final CobaltUser user = CobaltUser.getAuthenticatedUser();

        final List<String> buffer = new ArrayList<>();
        for (final SecurityRole role : roles) {
            buffer.add(role.toString());
        }
        return (user != null) ? user.isInRole(buffer.toArray(new String[buffer.size()])) : false;
    }

    /**
     * Returns true if the currently authenticated user can
     * stop or restart a batch job.
     * @param usernameThatStartedTheJob the user who wants to stop or restart a job, may be null
     */
    public static boolean isUserAuthorizedToStopOrRestartBatchJob(final String usernameThatStartedTheJob) {
//		log.debug("Username that started the job: " + usernameThatStartedTheJob);
        final CobaltUser user = CobaltUser.getAuthenticatedUser();
//		log.debug("Current authenticated user: " + user);
        if (user == null) { // if not authenticated
//			log.debug("Null user - not authenticated.");
            return false;
        }
        if (user.isInRole(SecurityRole.ROLE_SUPERUSER.toString())) { // if they are a superuser
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
