package com.thomsonreuters.uscl.ereader.mgr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class EBookManagerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LdapAuthenticationProvider ldsLdapAuthenticationProvider;
    @Autowired
    private AuthenticationProvider testingAuthenticationProvider;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldsLdapAuthenticationProvider)
            .authenticationProvider(testingAuthenticationProvider);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.exceptionHandling().accessDeniedPage("/accessDenied.mvc");

        http.formLogin().loginPage("/login.mvc")
            .failureUrl("/loginFail.mvc")
            .defaultSuccessUrl("/afterAuthentication.mvc", true);

        http.authorizeRequests()
            .antMatchers("/js/**",
                         "/theme/**",
                         "/service/**",
                         "/smokeTest.mvc",
                         "/login.mvc",
                         "/loginFail.mvc",
                         "/accessDenied.mvc",
                         "/afterLogout.mvc",
                         "/dismissOutage.mvc")
            .permitAll()

            .antMatchers("/bookDefinitionDelete.mvc",
                         "/bookDefinitionRestore.mvc",
                         "/adminJuris*",
                         "/adminPublish*",
                         "/adminState*",
                         "/adminKeyword*",
                         "/adminBookLock*",
                         "/adminMisc*",
                         "/adminAuditBook*",
                         "/adminQualityReportsView*",
                         "/adminDocTypeMetricView.mvc",
                         "/adminDocTypeMetricEdit*",
                         "/proviewGroupDefinitionEdit.mvc",
                         "/proviewTitleRemove*",
                         "/proviewTitleDelete*")
            .hasRole("SUPERUSER")

            .antMatchers("/adminMain.mvc",
                         "/adminOutage*",
                         "/adminSupport*",
                         "/adminStartGenerator*",
                         "/adminStopGenerator*",
                         "/adminJobThrottleConfig.mvc")
            .hasAnyRole("SUPPORT", "SUPERUSER")

            .antMatchers("/bookDefinitionCreate.mvc",
                         "/bookDefinitionCopy.mvc",
                         "/generateEbookPreview.mvc",
                         "/generateBulkEbookPreview.mvc")
            .hasAnyRole("PUBLISHER", "PUBLISHER_PLUS", "SUPERUSER")

            .antMatchers("/proviewTitlePromote*")
            .hasAnyRole("PUBLISHER_PLUS", "SUPERUSER")

            .antMatchers("/bookDefinitionEdit.mvc")
            .hasAnyRole("EDITOR", "PUBLISHER", "PUBLISHER_PLUS", "SUPERUSER")

            .antMatchers("/**")
            .fullyAuthenticated();

        http.logout()
            .logoutUrl("/j_spring_security_logout")
            .logoutSuccessUrl("/afterLogout.mvc");
    }
}