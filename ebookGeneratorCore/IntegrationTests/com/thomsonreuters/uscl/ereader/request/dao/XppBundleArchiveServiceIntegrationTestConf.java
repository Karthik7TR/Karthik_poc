package com.thomsonreuters.uscl.ereader.request.dao;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.thomsonreuters.uscl.ereader",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "jpaTransactionManager")
public class XppBundleArchiveServiceIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public XppBundleArchiveServiceIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setAnnotatedClasses(XppBundleArchive.class));
    }
    @Bean
    public XppBundleArchiveService codeService(XppBundleArchiveDao xppBundleArchiveDao) {
        return new XppBundleArchiveService(xppBundleArchiveDao);
    }
}
