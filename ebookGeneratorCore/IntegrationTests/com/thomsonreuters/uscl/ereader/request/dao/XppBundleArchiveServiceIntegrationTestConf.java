package com.thomsonreuters.uscl.ereader.request.dao;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
@EnableTransactionManagement
public class XppBundleArchiveServiceIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public XppBundleArchiveServiceIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setAnnotatedClasses(XppBundleArchive.class));
    }

    @Bean
    public XppBundleArchiveDao xppBundleArchiveDao() {
        return new XppBundleArchiveDaoImpl(sessionFactory());
    }

    @Bean
    public XppBundleArchiveService xppBundleArchiveService() {
        return new XppBundleArchiveService(xppBundleArchiveDao());
    }
}
