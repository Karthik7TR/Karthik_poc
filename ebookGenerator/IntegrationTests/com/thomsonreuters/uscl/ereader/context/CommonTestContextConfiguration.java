package com.thomsonreuters.uscl.ereader.context;

import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.TestBookFileSystemImpl;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtilImpl;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtil;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtilImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = {
        "com.thomsonreuters.uscl.ereader.xpp",
        "com.thomsonreuters.uscl.ereader.common",
        "com.thomsonreuters.uscl.ereader.assemble",
        "com.thomsonreuters.uscl.ereader.deliver"},
    excludeFilters = {
        @ComponentScan.Filter(value = {BookFileSystem.class, ProviewTitleService.class}, type = FilterType.ASSIGNABLE_TYPE)})
@PropertySources({
    @PropertySource("file:WebContent/WEB-INF/spring/properties/xpp.properties"),
    @PropertySource("file:IntegrationTests/WEB-INF/spring/properties/default-spring.properties")})
@Profile("IntegrationTests")
public class CommonTestContextConfiguration
{
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "bookFileSystem")
    public BookFileSystem bookFileSystem()
    {
        return new TestBookFileSystemImpl();
    }

    @Bean
    public VersionUtil versionUtil()
    {
        return new VersionUtilImpl();
    }

    @Bean
    public BookTitlesUtil bookTitlesUtil()
    {
        return new BookTitlesUtilImpl();
    }

    @Bean
    public ProviewClient proviewClient()
    {
        return Mockito.mock(ProviewClient.class);
    }

    @Bean
    public ProviewTitleService proviewTitleService()
    {
        return Mockito.mock(ProviewTitleService.class);
    }
}
