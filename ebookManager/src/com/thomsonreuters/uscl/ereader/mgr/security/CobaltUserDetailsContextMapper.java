package com.thomsonreuters.uscl.ereader.mgr.security;

import java.util.Collection;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.log4j.Logger;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CobaltUserDetailsContextMapper implements UserDetailsContextMapper {
	private static final Logger log = Logger.getLogger(CobaltUserDetailsContextMapper.class);
	private CobaltUserAttributesMapper userEntryAttributesMapper;
	
	public CobaltUserDetailsContextMapper(CobaltUserAttributesMapper userEntryAttributesMapper) {
		this.userEntryAttributesMapper = userEntryAttributesMapper;
	}
	
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
										  Collection<? extends GrantedAuthority> authorities) {
		Attributes userAttrs = ctx.getAttributes();
		CobaltUser user = null;
		try {
			user = (CobaltUser) userEntryAttributesMapper.mapFromAttributes(userAttrs);
			//log.debug("Mapped to User: " + user);
		} catch (NamingException e) {
			log.error(e);
		}
		return user;
	}

	@Override
	public void mapUserToContext(UserDetails paramUserDetails,
			DirContextAdapter paramDirContextAdapter) {
		throw new NotImplementedException();
	}
}
