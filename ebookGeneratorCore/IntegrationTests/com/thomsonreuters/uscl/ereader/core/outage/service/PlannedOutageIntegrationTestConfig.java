package com.thomsonreuters.uscl.ereader.core.outage.service;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
@EnableTransactionManagement
@EnableJpaRepositories
public class PlannedOutageIntegrationTestConfig extends AbstractDatabaseIntegrationTestConfig {
    public PlannedOutageIntegrationTestConfig() {
        super(sessionFactory -> sessionFactory.setPackagesToScan("com.thomsonreuters.uscl.ereader"));
    }

    @Bean
    public OutageDao outageDao() {
        return new OutageDaoImpl(sessionFactory());
    }

    @Bean
    public OutageService outageService() {
        return new OutageServiceImpl(outageDao());
    }
}
