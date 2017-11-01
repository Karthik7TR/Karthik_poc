package com.thomsonreuters.uscl.ereader.stats;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDaoImpl;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsServiceImpl;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtilImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
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
    public PublishingStatsService publishingStatsService() {
        return new PublishingStatsServiceImpl(publishingStatsDao(), publishingStatsUtil());
    }
}
