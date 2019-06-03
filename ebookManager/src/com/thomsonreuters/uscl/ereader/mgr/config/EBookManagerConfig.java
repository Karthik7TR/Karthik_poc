package com.thomsonreuters.uscl.ereader.mgr.config;

import java.util.Arrays;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.mgr.cleanup.JobCleaner;
import com.thomsonreuters.uscl.ereader.mgr.security.CobaltUserAttributesMapper;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
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
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
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
public class EBookManagerConfig extends WebMvcConfigurerAdapter {
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
    public AttributesMapper cobaltUserAttributesMapper(@Qualifier("environmentName") final String environment,
                                                       @Value("#{${prod.user.roles}}") final Map<String, String> productionGroupToRoleMap,
                                                       @Value("#{${nonprod.user.roles}}") final Map<String, String> nonProductionGroupToRoleMap) {
        return new CobaltUserAttributesMapper(environment, productionGroupToRoleMap, nonProductionGroupToRoleMap);
    }

    //Following beans should be moved to the EBookManagerAuthConfig
    @Bean
    public LdapContextSource ldsLdapContextSource(@Value("#{${ldap.lds.config}}") final  Map<String, String> ldsLdapConfig) {
        return createLdapContextSource(ldsLdapConfig);
    }

    @Bean
    public LdapContextSource tlrLdapContextSource(@Value("#{${ldap.tlr.config}}") final Map<String, String> tlrLdapConfig) {
        return createLdapContextSource(tlrLdapConfig);
    }

    @Bean
    public LdapContextSource tenLdapContextSource(@Value("#{${ldap.ten.config}}") final Map<String, String> tenLdapConfig) {
        return createLdapContextSource(tenLdapConfig);
    }

    private LdapContextSource createLdapContextSource(final Map<String, String> ldapConfig) {
        final LdapContextSource source = new LdapContextSource();
        source.setUrl(ldapConfig.get("url"));
        source.setBase(ldapConfig.get("base"));
        source.setUserDn(ldapConfig.get("userDn"));
        source.setPassword(ldapConfig.get("password"));
        return source;
    }
}
