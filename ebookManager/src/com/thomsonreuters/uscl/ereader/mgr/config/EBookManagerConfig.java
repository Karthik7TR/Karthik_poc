package com.thomsonreuters.uscl.ereader.mgr.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.mgr.cleanup.JobCleaner;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUserAttributesMapper;
import com.thomsonreuters.uscl.ereader.mgr.security.TestingAuthenticationProvider;
import com.thomsonreuters.uscl.ereader.mgr.security.TestingUserDetailsService;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

@Configuration
@EnableWebMvc
@EnableScheduling
@Import({EBookManagerSecurityConfig.class, EBookManagerJPAConfig.class, EBookManagerAuthConfig.class})
@ImportResource({"classpath:spring/*.xml", "/WEB-INF/spring/*.xml"})
@ComponentScan("com.thomsonreuters.uscl.ereader")
//@PropertySources({
//    @PropertySource("classpath:spring/properties/default-spring.properties"),
//    @PropertySource("classpath:spring/properties/workstation-spring.properties"),
//    @PropertySource("classpath:eBookManager.properties")
//})
public class EBookManagerConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsContextMapper userDetailsContextMapper;

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(
            @Qualifier("miscConfigMessageConverter") final HttpMessageConverter<?> miscConfigMessageConverter,
            @Qualifier("outageMessageConverter") final HttpMessageConverter<?> outageMessageConverter,
            @Qualifier("mappingJacksonHttpMessageConverter") final HttpMessageConverter<?> mappingJacksonHttpMessageConverter,
            @Qualifier("stringHttpMessageConverter") final HttpMessageConverter<?> stringHttpMessageConverter) {
        final RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
        requestMappingHandlerAdapter.setMessageConverters(
            Arrays.asList(
                miscConfigMessageConverter,
                outageMessageConverter,
                mappingJacksonHttpMessageConverter,
                stringHttpMessageConverter));
        return requestMappingHandlerAdapter;
    }

    @Bean
    public TilesConfigurer tilesConfigurer() {
        final TilesConfigurer configurer = new TilesConfigurer();
        configurer.setDefinitions("/WEB-INF/tiles/tiles-defs.xml");
        return configurer;
    }

    @Bean
    public ViewResolver tilesViewResolver() {
        final UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
        viewResolver.setOrder(1);
        viewResolver.setViewClass(TilesView.class);
        viewResolver.setViewNames("_*");
        return viewResolver;
    }

    @Bean
    public ViewResolver restViewResolver() {
        final BeanNameViewResolver viewResolver = new BeanNameViewResolver();
        viewResolver.setOrder(2);
        return viewResolver;
    }

    @Bean
    public ViewResolver internalResourceViewResolver() {
        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setOrder(3);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean
    public String environmentName(@Value("${environment}") final String environment) {
        return environment;
    }

    @Bean
    public MessageSource messageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor() {
        return new MessageSourceAccessor(messageSource());
    }

    @Bean
    public DefaultAnnotationHandlerMapping defaultAnnotationHandlerMapping() {
        return new DefaultAnnotationHandlerMapping();
    }

    @Bean
    public static PropertyPlaceholderConfigurer propertyConfigurer() {
        final PropertyPlaceholderConfigurer propertyConfigurer = new PropertyPlaceholderConfigurer();
        propertyConfigurer.setLocations(new ClassPathResource("spring/properties/default-spring.properties"),
            new ClassPathResource(String.format("spring/properties/%s-spring.properties", System.getProperty("environment"))),
            new ClassPathResource("eBookManager.properties"));
        propertyConfigurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return propertyConfigurer;
    }

    /*The following beans should be defined using java annotations
     * currently it doesn't work since it can't read placeholder properties.
     * Probably it should be fixed when we add @PropertySource annotation,
     * but currently xmls in Core projects don't allow us to do this.
     * However it does't work on the servers but on local machines it does.
     * Investigation needed
     */
    @Bean
    public JobCleaner jobCleaner(final ManagerService managerService,
                                 @Value("${cleanup.jobs.older.than.this.many.days.old}")final int cleanJobsGreaterThanThisManyDaysOld,
                                 @Value("${clean.planned.outages.greater.than.this.many.days.old}")final int cleanPlannedOutagesGreaterThanThisManyDaysOld,
                                 @Value("${number.last.major.version.kept}")final int numberLastMajorVersionKept,
                                 @Value("${days.before.docmetadata.delete}")final int daysBeforeDocMetadataDelete,
                                 @Value("${cleanup.cwb.files.older.than.this.many.days.old}")final int cleanCwbFilesGreaterThanThisManyDaysOld) {
        return new JobCleaner(managerService, cleanJobsGreaterThanThisManyDaysOld,
            cleanPlannedOutagesGreaterThanThisManyDaysOld, numberLastMajorVersionKept,
            daysBeforeDocMetadataDelete, cleanCwbFilesGreaterThanThisManyDaysOld);
    }

    @Bean
    public AttributesMapper cobaltUserAttributesMapper() {
        return new CobaltUserAttributesMapper(System.getProperty("environment"), productionGroupToRoleMap(), nonProductionGroupToRoleMap());
    }

    //Following beans should be moved to the EBookManagerAuthConfig
    @Bean
    public LdapContextSource ldsLdapContextSource() {
        return createLdapContextSource(ldsLdapConfig());
    }

    @Bean
    public LdapContextSource tlrLdapContextSource() {
        return createLdapContextSource(tlrLdapConfig());
    }

    @Bean
    public LdapContextSource tenLdapContextSource() {
        return createLdapContextSource(tenLdapConfig());
    }

    private LdapContextSource createLdapContextSource(final Map<String, String> ldapConfig) {
        final LdapContextSource source = new LdapContextSource();
        source.setUrl(ldapConfig.get("url"));
        source.setBase(ldapConfig.get("base"));
        source.setUserDn(ldapConfig.get("userDn"));
        source.setPassword(ldapConfig.get("password"));
        return source;
    }

    @Bean
    public FilterBasedLdapUserSearch ldsLdapUserSearchFilter() {
        return createFilterBasedLdapUserSearch(ldsLdapContextSource());
    }

    @Bean
    public FilterBasedLdapUserSearch tlrLdapUserSearchFilter() {
        return createFilterBasedLdapUserSearch(tlrLdapContextSource());
    }

    @Bean
    public FilterBasedLdapUserSearch tenLdapUserSearchFilter() {
        return createFilterBasedLdapUserSearch(tenLdapContextSource());
    }

    private FilterBasedLdapUserSearch createFilterBasedLdapUserSearch(final LdapContextSource ldapContextSource) {
        final FilterBasedLdapUserSearch filterBasedLdapUserSearch = new FilterBasedLdapUserSearch("", "(cn={0})", ldapContextSource);
        filterBasedLdapUserSearch.setSearchTimeLimit(15000);
        return filterBasedLdapUserSearch;
    }

    @Bean
    public BindAuthenticator ldsBindAuthenticator() {
        return createBindAuthenticator(ldsLdapContextSource(), ldsLdapUserSearchFilter());
    }

    @Bean
    public BindAuthenticator tlrBindAuthenticator() {
        return createBindAuthenticator(tlrLdapContextSource(), tlrLdapUserSearchFilter());
    }

    @Bean
    public BindAuthenticator tenBindAuthenticator() {
        return createBindAuthenticator(tenLdapContextSource(), tenLdapUserSearchFilter());
    }

    private BindAuthenticator createBindAuthenticator(final LdapContextSource ldapContextSource,
                                                      final FilterBasedLdapUserSearch filterBasedLdapUserSearch) {
        final BindAuthenticator bindAuthenticator = new BindAuthenticator(ldapContextSource);
        bindAuthenticator.setUserSearch(filterBasedLdapUserSearch);
        return bindAuthenticator;
    }

    @Bean
    public LdapAuthenticationProvider ldsLdapAuthenticationProvider() {
        return createLdapAuthenticationProvider(userDetailsContextMapper, ldsBindAuthenticator());
    }

    @Bean
    public LdapAuthenticationProvider tlrLdapAuthenticationProvider() {
        return createLdapAuthenticationProvider(userDetailsContextMapper, tlrBindAuthenticator());
    }

    @Bean
    public LdapAuthenticationProvider tenLdapAuthenticationProvider() {
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

    //Should be in eBookManagerSecurityConfig
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldsLdapAuthenticationProvider())
            .authenticationProvider(testingAuthenticationProvider(System.getProperty("environment")));
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

    //it is in the properties file
    private  Map<String, String> ldsLdapConfig() {
        final Map<String, String> ldapConfig = new HashMap<>();
        ldapConfig.put("url", "ldaps://lds.int.thomsonreuters.com:50001");
        ldapConfig.put("base", "ou=Users,DC=lds,DC=thomsonreuters,DC=com");
        ldapConfig.put("userDn", "CN=s.cobalt.ebookbuilder,OU=Service Accounts,DC=lds,DC=thomsonreuters,DC=com");
        ldapConfig.put("password", "Iaw*:C@DAA");
        return ldapConfig;
    }

    private  Map<String, String> tlrLdapConfig() {
        final Map<String, String> ldapConfig = new HashMap<>();
        ldapConfig.put("url", "ldap://tlradldap.int.westgroup.com:389");
        ldapConfig.put("base", "ou=West-TLRCorp,dc=TLR,dc=Thomson,dc=com");
        ldapConfig.put("userDn", "CN=svcCTWebSphere,OU=Service - Application Accounts,OU=Administrative Accounts,OU=West-TLRCorp Administration,OU=West-TLRCorp,DC=TLR,DC=Thomson,DC=com");
        ldapConfig.put("password", "CodeDev3");
        return ldapConfig;
    }

    private  Map<String, String> tenLdapConfig() {
        final Map<String, String> ldapConfig = new HashMap<>();
        ldapConfig.put("url", "ldap://tenadldap.int.thomsonreuters.com:389");
        ldapConfig.put("base", "DC=ten,DC=thomsonreuters,DC=com");
        ldapConfig.put("userDn", "CN=s.cobalt.ebookbuilder,OU=Service Accounts,OU=Admin,OU=MSP01,DC=ten,DC=thomsonreuters,DC=com");
        ldapConfig.put("password", "Iaw*:C@DAA");
        return ldapConfig;
    }

    private  Map<String, String> productionGroupToRoleMap() {
        final Map<String, String> ldapConfig = new HashMap<>();
        ldapConfig.put("P-West-EBOOKBUILDER_GUEST_PROD.*", "ROLE_GUEST");
        ldapConfig.put("P-West-EBOOKBUILDER_EDITOR_PROD.*", "ROLE_EDITOR");
        ldapConfig.put("P-West-EBOOKBUILDER_PUBLISHER_PROD.*", "ROLE_PUBLISHER");
        ldapConfig.put("P-West-EBOOKBUILDER_PUBLISHER_PLUS_PROD.*", "ROLE_PUBLISHER_PLUS");
        ldapConfig.put("P-West-EBOOKBUILDER_SUPERUSER_PROD.*", "ROLE_SUPERUSER");
        ldapConfig.put("P-West-EBOOKBUILDER_SUPPORT_PROD.*", "ROLE_SUPPORT");
        return ldapConfig;
    }

    private  Map<String, String> nonProductionGroupToRoleMap() {
        final Map<String, String> ldapConfig = new HashMap<>();
        ldapConfig.put("P-West-EBOOKBUILDER_GUEST_NONPROD.*", "ROLE_GUEST");
        ldapConfig.put("P-West-EBOOKBUILDER_EDITOR_NONPROD.*", "ROLE_EDITOR");
        ldapConfig.put("P-West-EBOOKBUILDER_PUBLISHER_NONPROD.*", "ROLE_PUBLISHER");
        ldapConfig.put("P-West-EBOOKBUILDER_PUBLISHER_PLUS_NONPROD.*", "ROLE_PUBLISHER_PLUS");
        ldapConfig.put("P-West-EBOOKBUILDER_SUPERUSER_NONPROD.*", "ROLE_SUPERUSER");
        ldapConfig.put("P-West-EBOOKBUILDER_SUPPORT_NONPROD.*", "ROLE_SUPPORT");
        return ldapConfig;
    }
}
