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

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Map the attributes for a Thomson Reuters LDAP user entry to a User object.
 */
@Slf4j
public class CobaltUserAttributesMapper implements AttributesMapper {
    private static final String ATTR_COMMON_NAME = "cn";
    private static final String ATTR_EMAIL_ADDR = "mail";
    private static final String ATTR_FIRST_NAME = "givenName";
    private static final String ATTR_GROUP = "memberOf";
    private static final String ATTR_LAST_NAME = "sn";
    private static final String ATTR_USERNAME = ATTR_COMMON_NAME;

    /** KEY=a regular expression for a physical LDAP group name, VALUE=logical role name (like ROLE_SUPERUSER) */
    private final Map<String, String> groupToRoleMap;

    /**
     * Constructor for the LDAP user entry attribute mapper.  Maps the user entry into a POJO.  For group/role names,
     * uses either a prod or non-prod version depending on the current environment name.
     * @param groupToRoleMap is the mapping between physical group names and logical role/authority names.
     * The key is a Java regular expression, the value is the corresponding role name for a match.
     */
    public CobaltUserAttributesMapper(
            @Qualifier("environmentName") final String environment,
            @Value("#{${prod.user.roles}}") final Map<String, String> productionGroupToRoleMap,
            @Value("#{${nonprod.user.roles}}") final Map<String, String> nonProductionGroupToRoleMap) {
        if (CoreConstants.PROD_ENVIRONMENT_NAME.equalsIgnoreCase(environment)) {
            groupToRoleMap = productionGroupToRoleMap;
        } else {
            groupToRoleMap = nonProductionGroupToRoleMap;
        }
    }

    @Override
    public Object mapFromAttributes(final Attributes attributes) throws NamingException {
        final String username = attrToString(attributes.get(ATTR_USERNAME));
        final String firstName = attrToString(attributes.get(ATTR_FIRST_NAME));
        final String lastName = attrToString(attributes.get(ATTR_LAST_NAME));
        final String emailAddr = attrToString(attributes.get(ATTR_EMAIL_ADDR));
        // Load the physical LDAP groups to then map them to logical roles/authorities
        final Collection<String> groups = loadLdapGroups(attributes);
        final Collection<GrantedAuthority> authorities = mapGroupsToRoles(groups);
        return new CobaltUser(username, firstName, lastName, emailAddr, authorities);
    }

    private static String attrToString(final Attribute attr) throws NamingException {
        return (attr != null) ? (String) attr.get() : null;
    }

    /**
     * Fetch the physical group names that have been assigned to the user.
     * These will then in turn be mapped to logical role/authority names.
     * @param attributes the attributed of the user LDAP directory entry
     */
    private Collection<String> loadLdapGroups(final Attributes attributes) throws NamingException {
        final Collection<String> groups = new ArrayList<>();
        final Attribute memberOf = attributes.get(ATTR_GROUP);
        if (memberOf != null) {
            final NamingEnumeration<?> ne = memberOf.getAll();
            while (ne.hasMore()) {
                final String groupDnString = (String) ne.next(); // Fully qualified DN of the group
                // Parse the full DN to get just the value for the CN attribute
                final DistinguishedName groupDn = new DistinguishedName(groupDnString);
                final LdapRdn cn = groupDn.getLdapRdn(ATTR_COMMON_NAME);
                if (cn != null) {
                    final String groupName = cn.getValue();
                    groups.add(groupName);
                }
            }
        } else {
            log.warn(
                "Expected to find a memberOf attribute, but none was present for LDAP entry: "
                    + ReflectionToStringBuilder.toString(attributes, ToStringStyle.SHORT_PREFIX_STYLE));
        }
        log.debug("Loaded LDAP groups: " + groups);
        return groups;
    }

    /**
     * Map physical group names retrieved from service to the logical roles that they represent.
     * @param groups collection of String group names from directory server, may not be null
     * @return collection of GrantedAuthority objects that represent the logical user roles in the application.
     */
    private Collection<GrantedAuthority> mapGroupsToRoles(final Collection<String> groups) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String group : groups) {
            final String role = matchGroup(group);
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
    private String matchGroup(final String group) {
        final Set<String> groupNameRegularExpressions = groupToRoleMap.keySet();
        for (final String groupNameExpr : groupNameRegularExpressions) {
            if (Pattern.matches(groupNameExpr, group)) {
                return groupToRoleMap.get(groupNameExpr);
            }
        }
        return null;
    }
}
