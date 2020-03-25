package com.thomsonreuters.uscl.ereader.mgr.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.OperationNotSupportedException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControlExtractor;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

@Slf4j
public class EBookBindAuthenticator extends BindAuthenticator {

    public EBookBindAuthenticator(final BaseLdapPathContextSource contextSource) {
        super(contextSource);
    }

    @Override
    public DirContextOperations authenticate(final Authentication authentication) {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                "Can only process UsernamePasswordAuthenticationToken objects");
        final String username = authentication.getName();
        final String password = (String) authentication.getCredentials();

        if (!StringUtils.hasLength(password)) {
            log.debug("Rejecting empty password for user " + username);
            throw new BadCredentialsException("Empty Password");
        }

        final LdapUserSearch userSearch = getUserSearch();
        DirContextOperations user = null;

        if (userSearch != null) {
            final DirContextOperations userFromSearch = userSearch.searchForUser(username);
            user = bindWithDn(userFromSearch.getDn().toString(), username, password,
                    userFromSearch.getAttributes());
        }

        if (user == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        return user;
    }

    private DirContextOperations bindWithDn(final String userDnStr, final String username,
        final String password, final Attributes attributes) {
        final BaseLdapPathContextSource ctxSource = (BaseLdapPathContextSource) getContextSource();
        final DistinguishedName userDn = new DistinguishedName(userDnStr);
        final DistinguishedName fullDn = new DistinguishedName(userDn);
        fullDn.prepend(ctxSource.getBaseLdapPath());
        DirContext ctx = null;

        try {
            ctx = getContextSource().getContext(fullDn.toString(), password);
            final PasswordPolicyControl ppolicy = PasswordPolicyControlExtractor.extractControl(ctx);
            final DirContextAdapter result = new DirContextAdapter(attributes, userDn,
                    ctxSource.getBaseLdapPath());

            if (ppolicy != null) {
                result.setAttributeValue(ppolicy.getID(), ppolicy);
            }

            return result;
        } catch (NamingException e) {
            handleException(e, userDnStr, username);
        } finally {
            LdapUtils.closeContext(ctx);
        }

        return null;
    }

    private void handleException(final NamingException exception, final String userDnStr,
        final String username) {
        if ((exception instanceof AuthenticationException)
                || (exception instanceof OperationNotSupportedException)) {
            handleBindException(userDnStr, username, exception);
        } else {
            throw exception;
        }
    }
}
