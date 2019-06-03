package com.thomsonreuters.uscl.ereader.mgr.security;

import java.util.Collection;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;

/**
 * Object mapper used to create a user object from the attributes contained within a specified LDAP directory context.
 * Delegates to a standard LDAP AttributesMapper to accomplish this.
 */
@Component("userDetailsContextMapper")
@Slf4j
public class CobaltUserDetailsContextMapper implements UserDetailsContextMapper {
    private static final Logger log = LogManager.getLogger(CobaltUserDetailsContextMapper.class);
    private final AttributesMapper userEntryAttributesMapper;

    @Autowired
    public CobaltUserDetailsContextMapper(final AttributesMapper userEntryAttributesMapper) {
        this.userEntryAttributesMapper = userEntryAttributesMapper;
    }

    @Override
    public UserDetails mapUserFromContext(
        final DirContextOperations userDirectoryContext,
        final String username,
        final Collection<? extends GrantedAuthority> authorities) {
        log.debug("Creating user object from LDAP entry at DN: " + userDirectoryContext.getDn());
        final Attributes userAttrs = userDirectoryContext.getAttributes();
        CobaltUser user = null;
        try {
            user = (CobaltUser) userEntryAttributesMapper.mapFromAttributes(userAttrs);
        } catch (final NamingException e) {
            log.error("Unable to map user entry for username=" + username, e);
        }
        return user;
    }

    @Override
    public void mapUserToContext(final UserDetails paramUserDetails, final DirContextAdapter paramDirContextAdapter) {
        throw new NotImplementedException("Not Implemented");
    }
}
