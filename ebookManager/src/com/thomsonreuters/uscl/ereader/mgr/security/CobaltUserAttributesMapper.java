/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;

/**
 * Map the attributes for a Thomson Reuters LDAP user entry to a User object.
 */
public class CobaltUserAttributesMapper implements AttributesMapper {
	private static final Logger log = LogManager.getLogger(CobaltUserAttributesMapper.class);
	private static final String ATTR_COMMON_NAME = "cn";
	private static final String ATTR_EMAIL_ADDR = "mail";
	private static final String ATTR_FIRST_NAME = "givenName";
	private static final String ATTR_GROUP = "memberOf";
	private static final String ATTR_LAST_NAME = "sn";
	private static final String ATTR_USERNAME = ATTR_COMMON_NAME;
	
	/** KEY=a regular expression for a physical LDAP group name, VALUE=logical role name (like ROLE_SUPERUSER) */
	private Map<String,String> groupToRoleMap;
	
	/**
	 * Constructor for the LDAP user entry attribute mapper.  Maps the user entry into a POJO.  For group/role names,
	 * uses either a prod or non-prod version depending on the current environment name.
	 * @param groupToRoleMap is the mapping between physical group names and logical role/authority names.
	 * The key is a Java regular expression, the value is the corresponding role name for a match.
	 */
	public CobaltUserAttributesMapper(String environmentName,
									  Map<String,String> productionGroupToRoleMap,
									  Map<String,String> nonProductionGroupToRoleMap) {
		if (CoreConstants.PROD_ENVIRONMENT_NAME.equalsIgnoreCase(environmentName)) {
			this.groupToRoleMap = productionGroupToRoleMap;
		} else {
			this.groupToRoleMap = nonProductionGroupToRoleMap;	
		}
	}
	
	@Override
	public Object mapFromAttributes(Attributes attributes) throws NamingException {
		String username = attrToString(attributes.get(ATTR_USERNAME));
		String firstName = attrToString(attributes.get(ATTR_FIRST_NAME));
		String lastName = attrToString(attributes.get(ATTR_LAST_NAME));
		String emailAddr = attrToString(attributes.get(ATTR_EMAIL_ADDR));
		// Load the physical LDAP groups to then map them to logical roles/authorities
		Collection<String> groups = loadLdapGroups(attributes);
		Collection<GrantedAuthority> authorities = mapGroupsToRoles(groups);
		CobaltUser user = new CobaltUser(username, firstName, lastName, emailAddr, authorities);
		return user;
	}
	
	private static String attrToString(Attribute attr) throws NamingException {
		return (attr != null) ? (String) attr.get() : null;
	}
	
	/**
	 * Fetch the physical group names that have been assigned to the user.
	 * These will then in turn be mapped to logical role/authority names.
	 * @param attributes the attributed of the user LDAP directory entry
	 */
	@SuppressWarnings("rawtypes")
	private Collection<String> loadLdapGroups(Attributes attributes) throws NamingException {
		Collection<String> groups = new ArrayList<String>();
		Attribute memberOf = attributes.get(ATTR_GROUP);
		if (memberOf != null) {
			NamingEnumeration ne = memberOf.getAll();
			while (ne.hasMore()) {
				String groupDnString = (String) ne.next();  // Fully qualified DN of the group
				// Parse the full DN to get just the value for the CN attribute
				DistinguishedName groupDn = new DistinguishedName(groupDnString);
				LdapRdn cn = groupDn.getLdapRdn(ATTR_COMMON_NAME);
				if (cn != null) {
					String groupName = cn.getValue();
					groups.add(groupName);
				}
			}
		} else {
			log.warn("Expected to find a memberOf attribute, but none was present for LDAP entry: " + ReflectionToStringBuilder.toString(attributes, ToStringStyle.SHORT_PREFIX_STYLE));
		}
		log.debug("Loaded LDAP groups: " + groups);
		return groups;
	}
	
	/**
	 * Map physical group names retrieved from service to the logical roles that they represent.
	 * @param groups collection of String group names from directory server, may not be null
	 * @return collection of GrantedAuthority objects that represent the logical user roles in the application.
	 */
	private Collection<GrantedAuthority> mapGroupsToRoles(Collection<String> groups) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String group : groups) {
			String role = matchGroup(group);
			if (role != null) {
				authorities.add(new SimpleGrantedAuthority(role));
			}
		}
		return authorities;
	}
	
	/**
	 * Find the matching role name for a group Java regular expression (RE).
	 * Example, for groupToRoleMap with entry key "P-West-LEGO_SWAT.*" and value "ROLE_SWAT",
	 * a group name of "P-West-LEGO_SWAT_DEV" would cause "ROLE_SWAT" to be returned.
	 * @param group physical group name to find within the set of regular expressions in the groupToRoleMap.
	 * @return the role name for the RE key if a match is found.
	 */
	private String matchGroup(String group) {
		Set<String> groupNameRegularExpressions = groupToRoleMap.keySet();
		for (String groupNameExpr : groupNameRegularExpressions) {
			if (Pattern.matches(groupNameExpr, group)) {
				return groupToRoleMap.get(groupNameExpr);
			}
		}
		return null;
	}
}
