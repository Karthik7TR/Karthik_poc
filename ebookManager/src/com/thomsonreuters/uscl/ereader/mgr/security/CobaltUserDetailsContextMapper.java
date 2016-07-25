/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.security;

import java.util.Collection;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
/**
 * Object mapper used to create a user object from the attributes contained within a specified LDAP directory context.
 * Delegates to a standard LDAP AttributesMapper to accomplish this.
 */
public class CobaltUserDetailsContextMapper implements UserDetailsContextMapper {
	private static final Logger log = LogManager.getLogger(CobaltUserDetailsContextMapper.class);
	private AttributesMapper userEntryAttributesMapper;
	
	public CobaltUserDetailsContextMapper(AttributesMapper userEntryAttributesMapper) {
		this.userEntryAttributesMapper = userEntryAttributesMapper;
	}
	
	@Override
	public UserDetails mapUserFromContext(DirContextOperations userDirectoryContext, String username,
										  Collection<? extends GrantedAuthority> authorities) {
		log.debug("Creating user object from LDAP entry at DN: " + userDirectoryContext.getDn());
		Attributes userAttrs = userDirectoryContext.getAttributes();
		CobaltUser user = null;
		try {
			user = (CobaltUser) userEntryAttributesMapper.mapFromAttributes(userAttrs);
		} catch (NamingException e) {
			log.error("Unable to map user entry for username="+username, e);
		}
		return user;
	}

	@Override
	public void mapUserToContext(UserDetails paramUserDetails,
			DirContextAdapter paramDirContextAdapter) {
		throw new NotImplementedException();
	}
}
