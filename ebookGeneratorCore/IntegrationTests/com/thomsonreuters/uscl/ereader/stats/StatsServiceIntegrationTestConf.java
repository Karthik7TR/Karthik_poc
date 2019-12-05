package com.thomsonreuters.uscl.ereader.stats;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.dao.EBookAuditDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.dao.VersionIsbnDao;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnServiceImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandlerImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.SupersededProviewHandlerHelper;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDaoImpl;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsServiceImpl;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtilImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
@EnableJpaRepositories(
    basePackages = "com.thomsonreuters.uscl.ereader",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "jpaTransactionManager"
)
@EnableTransactionManagement
public class StatsServiceIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public StatsServiceIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setPackagesToScan(
            "com.thomsonreuters.uscl.ereader.sql",
            "com.thomsonreuters.uscl.ereader.stats",
            "com.thomsonreuters.uscl.ereader.core.book.domain",
            "com.thomsonreuters.uscl.ereader.request.domain"));
    }

    @Bean
    public PublishingStatsDao publishingStatsDao() {
        return new PublishingStatsDaoImpl(sessionFactory());
    }

    @Bean
    public PublishingStatsUtil publishingStatsUtil() {
        return new PublishingStatsUtilImpl();
    }

    @Bean
    public EBookAuditService eBookAuditService(VersionIsbnService versionIsbnService) {
        return new EBookAuditServiceImpl(new EBookAuditDaoImpl(sessionFactory()), versionIsbnService);
    }

    @Bean
    public VersionIsbnService versionIsbnService(VersionIsbnDao versionIsbnDao) {
        return new VersionIsbnServiceImpl(versionIsbnDao, bookDefinitionService());
    }

    @Bean
    public BookDefinitionService bookDefinitionService() {
        return new BookDefinitionServiceImpl(new BookDefinitionDaoImpl(sessionFactory()));
    }

    @Bean
    public ProviewClient proviewClient() {
        return new ProviewClientImpl();
    }

    @Bean
    public ProviewHandler proviewHandler() {
        final ProviewHandlerImpl proviewHandler = new ProviewHandlerImpl();
        proviewHandler.setProviewClient(proviewClient());
        return proviewHandler;
    }

    @Bean
    public SupersededProviewHandlerHelper supersededProviewHandlerHelper() {
        return new SupersededProviewHandlerHelper();
    }

    @Bean
    public PublishingStatsService publishingStatsService() {
        return new PublishingStatsServiceImpl(publishingStatsDao(), publishingStatsUtil());
    }
}
