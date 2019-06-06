package com.thomsonreuters.uscl.ereader.mgr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class EBookManagerLdapConfig {
    @Bean
    public LdapContextSource ldsLdapContextSource() {
        final LdapContextSource source = new LdapContextSource();
        source.setUrl("ldaps://lds.int.thomsonreuters.com:50001");
        source.setBase("ou=Users,DC=lds,DC=thomsonreuters,DC=com");
        source.setUserDn("CN=s.cobalt.ebookbuilder,OU=Service Accounts,DC=lds,DC=thomsonreuters,DC=com");
        source.setPassword("Iaw*:C@DAA");
        return source;
    }

    @Bean
    public LdapContextSource tlrLdapContextSource() {
        final LdapContextSource source = new LdapContextSource();
        source.setUrl("ldap://tlradldap.int.westgroup.com:389");
        source.setBase("ou=West-TLRCorp,dc=TLR,dc=Thomson,dc=com");
        source.setUserDn(
            "CN=svcCTWebSphere,OU=Service - Application Accounts,OU=Administrative Accounts,"
                + "OU=West-TLRCorp Administration,OU=West-TLRCorp,DC=TLR,DC=Thomson,DC=com");
        source.setPassword("CodeDev3");
        return source;
    }

    @Bean
    public LdapContextSource tenLdapContextSource() {
        final LdapContextSource source = new LdapContextSource();
        source.setUrl("ldap://tenadldap.int.thomsonreuters.com:389");
        source.setBase("DC=ten,DC=thomsonreuters,DC=com");
        source.setUserDn(
            "CN=s.cobalt.ebookbuilder,OU=Service Accounts,OU=Admin,OU=MSP01,DC=ten,DC=thomsonreuters,DC=com");
        source.setPassword("Iaw*:C@DAA");
        return source;
    }
}
