/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TestingUserDetailsService implements UserDetailsService {

	//private static Logger log = LogManager.getLogger(TestingUserDetailsService.class);
	/**
	 * User details where the username is also the role name that will be assigned.
	 * E.g. log in as "ROLE_SUPERUSER" and be put into that role.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//log.debug("username="+username);
		Collection<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
		roles.add(new SimpleGrantedAuthority(TestingAuthenticationProvider.mapGroupFromUsername(username).toString()));
		
		UserDetails user = new CobaltUser(username, "User", username, username+"@bogus.tr.com", roles);
		return user;
	}
}
