package com.thomsonreuters.uscl.ereader.mgr.config;

import java.util.Map;

import com.thomsonreuters.uscl.ereader.mgr.security.EBookBindAuthenticator;
import com.thomsonreuters.uscl.ereader.mgr.security.TestingAuthenticationProvider;
import com.thomsonreuters.uscl.ereader.mgr.security.TestingUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

@Configuration
@Slf4j
public class EBookManagerAuthConfig {
    private static final String URL_KEY = "url";
    private static final String BASE_KEY = "base";
    private static final String USER_DN_KEY = "userDn";
    private static final String PASSWORD_KEY = "password";

    @Value("#{${ldap.vds.config}}")
    private Map<String, String> vdsLdapConfig;

    @Bean
    public LdapContextSource vdsLdapContextSource() {
        return createLdapContextSource(vdsLdapConfig);
    }

    private LdapContextSource createLdapContextSource(final Map<String, String> ldapConfig) {
        String url = ldapConfig.get(URL_KEY);
        String userDn = ldapConfig.get(USER_DN_KEY);
        log.info("Creating LDAP context source with url={} and userDN={}", url, userDn);
        final LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(ldapConfig.get(BASE_KEY));
        source.setUserDn(userDn);
        source.setPassword(ldapConfig.get(PASSWORD_KEY));
        return source;
    }

    @Bean
    public FilterBasedLdapUserSearch vdsLdapUserSearchFilter() {
        return createFilterBasedLdapUserSearch(vdsLdapContextSource());
    }

    private FilterBasedLdapUserSearch createFilterBasedLdapUserSearch(final LdapContextSource ldapContextSource) {
        final FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch("",
                "(cn={0})", ldapContextSource);
        filterBasedLdapUserSearch.setSearchTimeLimit(15000);
        return filterBasedLdapUserSearch;
    }

    @Bean
    public EBookBindAuthenticator eBookBindAuthenticator() {
        return createBindAuthenticator(vdsLdapContextSource(), vdsLdapUserSearchFilter());
    }

    private EBookBindAuthenticator createBindAuthenticator(final LdapContextSource ldapContextSource,
                                                      final FilterBasedLdapUserSearch filterBasedLdapUserSearch) {
        final EBookBindAuthenticator bindAuthenticator = new EBookBindAuthenticator(ldapContextSource);
        bindAuthenticator.setUserSearch(filterBasedLdapUserSearch);
        return bindAuthenticator;
    }

    @Bean
    public LdapAuthenticationProvider vdsLdapAuthenticationProvider(final UserDetailsContextMapper userDetailsContextMapper) {
        return createLdapAuthenticationProvider(userDetailsContextMapper, eBookBindAuthenticator());
    }

    private LdapAuthenticationProvider createLdapAuthenticationProvider(final UserDetailsContextMapper userDetailsContextMapper,
                                                                        final EBookBindAuthenticator bindAuthenticator) {
        final LdapAuthenticationProvider vdsLdapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator);
        vdsLdapAuthenticationProvider.setUserDetailsContextMapper(userDetailsContextMapper);
        return vdsLdapAuthenticationProvider;
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
