package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDao;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDaoImpl;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = "com.thomsonreuters.uscl.ereader",
        transactionManagerRef = "jpaTransactionManager")
public class JobRequestServiceIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public JobRequestServiceIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setPackagesToScan("com.thomsonreuters.uscl.ereader"));
    }

    @Bean
    public JobRequestService jobRequestService() {
        JobRequestServiceImpl jobRequestService = new JobRequestServiceImpl();
        jobRequestService.setJobRequestDao(jobRequestDao());
        return jobRequestService;
    }

    @Bean
    public BookDefinitionService bookDefinitionService() {
        return new BookDefinitionServiceImpl(bookDefinitionDao());
    }
    @Bean
    public JobRequestDao jobRequestDao() {
        return new JobRequestDaoImpl(sessionFactory());
    }
    @Bean
    public BookDefinitionDao bookDefinitionDao() {
        return new BookDefinitionDaoImpl(sessionFactory());
    }
}

