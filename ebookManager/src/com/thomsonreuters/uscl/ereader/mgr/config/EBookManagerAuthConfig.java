package com.thomsonreuters.uscl.ereader.mgr.config;

import com.thomsonreuters.uscl.ereader.mgr.security.TestingAuthenticationProvider;
import com.thomsonreuters.uscl.ereader.mgr.security.TestingUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

@Configuration
public class EBookManagerAuthConfig {
    @Autowired
    private LdapContextSource ldsLdapContextSource;
    @Autowired
    private LdapContextSource tlrLdapContextSource;
    @Autowired
    private LdapContextSource tenLdapContextSource;

    @Bean
    public FilterBasedLdapUserSearch ldsLdapUserSearchFilter() {
        return createFilterBasedLdapUserSearch(ldsLdapContextSource);
    }

    @Bean
    public FilterBasedLdapUserSearch tlrLdapUserSearchFilter() {
        return createFilterBasedLdapUserSearch(tlrLdapContextSource);
    }

    @Bean
    public FilterBasedLdapUserSearch tenLdapUserSearchFilter() {
        return createFilterBasedLdapUserSearch(tenLdapContextSource);
    }

    private FilterBasedLdapUserSearch createFilterBasedLdapUserSearch(final LdapContextSource ldapContextSource) {
        final FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch("", "(cn={0})", ldapContextSource);
        filterBasedLdapUserSearch.setSearchTimeLimit(15000);
        return filterBasedLdapUserSearch;
    }

    @Bean
    public BindAuthenticator ldsBindAuthenticator() {
        return createBindAuthenticator(ldsLdapContextSource, ldsLdapUserSearchFilter());
    }

    @Bean
    public BindAuthenticator tlrBindAuthenticator() {
        return createBindAuthenticator(tlrLdapContextSource, tlrLdapUserSearchFilter());
    }

    @Bean
    public BindAuthenticator tenBindAuthenticator() {
        return createBindAuthenticator(tenLdapContextSource, tenLdapUserSearchFilter());
    }

    private BindAuthenticator createBindAuthenticator(final LdapContextSource ldapContextSource,
                                                      final FilterBasedLdapUserSearch filterBasedLdapUserSearch) {
        final BindAuthenticator bindAuthenticator = new BindAuthenticator(ldapContextSource);
        bindAuthenticator.setUserSearch(filterBasedLdapUserSearch);
        return bindAuthenticator;
    }

    @Bean
    public LdapAuthenticationProvider ldsLdapAuthenticationProvider(final UserDetailsContextMapper userDetailsContextMapper) {
        return createLdapAuthenticationProvider(userDetailsContextMapper, ldsBindAuthenticator());
    }

    @Bean
    public LdapAuthenticationProvider tlrLdapAuthenticationProvider(final UserDetailsContextMapper userDetailsContextMapper) {
        return createLdapAuthenticationProvider(userDetailsContextMapper, tlrBindAuthenticator());
    }

    @Bean
    public LdapAuthenticationProvider tenLdapAuthenticationProvider(final UserDetailsContextMapper userDetailsContextMapper) {
        return createLdapAuthenticationProvider(userDetailsContextMapper, tenBindAuthenticator());
    }

    private LdapAuthenticationProvider createLdapAuthenticationProvider(final UserDetailsContextMapper userDetailsContextMapper,
                                                                        final BindAuthenticator bindAuthenticator) {
        final LdapAuthenticationProvider ldsLdapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator);
        ldsLdapAuthenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper);
        return ldsLdapAuthenticationProvider;
    }

    /*TESTING ONLY - NOT FOR PRODUCTION*/
    @Bean
    public UserDetailsService testingUserDetailsService() {
        return new TestingUserDetailsService();
    }

    @Bean
    public AuthenticationProvider testingAuthenticationProvider(@Qualifier("environmentName") final String environment) {
        final TestingAuthenticationProvider authenticationProvider = new TestingAuthenticationProvider();
        TestingAuthenticationProvider.setEnvironmentName(environment);
        authenticationProvider.setUserDetailsService(testingUserDetailsService());
        return authenticationProvider;
    }
}
