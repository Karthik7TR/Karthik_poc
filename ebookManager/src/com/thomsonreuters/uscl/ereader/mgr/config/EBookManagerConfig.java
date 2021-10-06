package com.thomsonreuters.uscl.ereader.mgr.config;

import java.util.Arrays;

import com.thomsonreuters.uscl.ereader.core.service.local.DevEnvironmentValidator;
import com.thomsonreuters.uscl.ereader.core.service.local.DevEnvironmentValidatorImpl;
import lombok.SneakyThrows;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
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
public class EBookManagerConfig extends WebMvcConfigurerAdapter {
    private static final long MB_2048 = 2L * 1024 * 1024 * 1024;

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
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(MB_2048);
        return commonsMultipartResolver;
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
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    @SneakyThrows
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        final PropertyPlaceholderConfigurer propertyConfigurer = new PropertyPlaceholderConfigurer();
        propertyConfigurer.setLocations(
            new ClassPathResource("spring/properties/default-spring.properties"),
            new ClassPathResource(String.format("spring/properties/%s-spring.properties", System.getProperty("environment"))),
            new ClassPathResource("eBookManager.properties"));
        propertyConfigurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
        return propertyConfigurer;
    }

    @Bean
    public DevEnvironmentValidator devEnvironmentValidator(@Qualifier("environmentName") final String environmentName) {
        return new DevEnvironmentValidatorImpl(environmentName);
    }
}
