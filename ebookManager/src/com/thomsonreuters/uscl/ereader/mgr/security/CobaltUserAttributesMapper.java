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
import org.apache.log4j.Logger;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Map the attributes for a Thomson Reuters LDAP user entry to a User object.
 */
public class CobaltUserAttributesMapper implements AttributesMapper {
	private static final Logger log = Logger.getLogger(CobaltUserAttributesMapper.class);
	/** KEY=a regular expression for a physical LDAP group name, VALUE=logical role name */
	private Map<String,String> groupToRoleMap;
	
	/**
	 * Constructor 
	 * @param groupToRoleMap is the mapping between physical group names and logical role/authority names.
	 * The key is a Java regular expression, the value is the corresponding role name for a match.
	 */
	public CobaltUserAttributesMapper(Map<String,String> groupToRoleMap) {
		this.groupToRoleMap = groupToRoleMap;
	}
	
	@Override
	public Object mapFromAttributes(Attributes attributes) throws NamingException {
		String username = attrToString(attributes.get("samaccountname"));
		String firstName = attrToString(attributes.get("givenName"));
		String lastName = attrToString(attributes.get("sn"));
		String email = attrToString(attributes.get("mail"));
		// Map the physical groups to logical roles
		Collection<String> groups = loadLdapGroups(attributes);
		Collection<GrantedAuthority> authorities = mapGroupsToRoles(groups);
		CobaltUser user = new CobaltUser(username, firstName, lastName, email, authorities);
		return user;
	}
	
	private static String attrToString(Attribute attr) throws NamingException {
		return (attr != null) ? (String) attr.get() : null;
	}
	
	@SuppressWarnings("rawtypes")
	private Collection<String> loadLdapGroups(Attributes attributes) throws NamingException {
		Collection<String> groups = new ArrayList<String>();
		Attribute memberOf = attributes.get("memberOf");
		if (memberOf != null) {
			NamingEnumeration ne = memberOf.getAll();
			while (ne.hasMore()) {
				String groupDnString = (String) ne.next();  // Fully qualified DN of the group
				// Parse the full DN to get just the value for the CN attribute
				DistinguishedName groupDn = new DistinguishedName(groupDnString);
				LdapRdn cn = groupDn.getLdapRdn("cn");
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
	 * Find the matching role name for a group RE.
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
