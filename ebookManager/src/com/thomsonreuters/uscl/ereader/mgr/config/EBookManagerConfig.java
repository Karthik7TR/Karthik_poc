package com.thomsonreuters.uscl.ereader.mgr.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.springframework.web.servlet.view.tiles2.TilesView;

@Configuration
@EnableWebMvc
@Import(EBookManagerLdapConfig.class)
@ImportResource({"classpath:spring/*.xml", "/WEB-INF/spring/*.xml"})
@ComponentScan("com.thomsonreuters.uscl.ereader.mgr")
public class EBookManagerConfig extends WebMvcConfigurerAdapter {
    @Autowired
    @Qualifier("miscConfigMessageConverter")
    private HttpMessageConverter<?> miscConfigMessageConverter;
    @Autowired
    @Qualifier("outageMessageConverter")
    private HttpMessageConverter<?> outageMessageConverter;
    @Autowired
    @Qualifier("mappingJacksonHttpMessageConverter")
    private HttpMessageConverter<?> mappingJacksonHttpMessageConverter;
    @Autowired
    @Qualifier("stringHttpMessageConverter")
    private HttpMessageConverter<?> stringHttpMessageConverter;
    @Value("${environment}")
    private String environment;

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
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
}
