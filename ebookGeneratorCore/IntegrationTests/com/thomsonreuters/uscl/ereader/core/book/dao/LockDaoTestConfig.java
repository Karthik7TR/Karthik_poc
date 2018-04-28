package com.thomsonreuters.uscl.ereader.core.book.dao;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.PublisherCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.PublisherCodeServiceImpl;
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
public class LockDaoTestConfig extends AbstractDatabaseIntegrationTestConfig {
    public LockDaoTestConfig() {
        super(sessionFactory -> sessionFactory.setPackagesToScan("com.thomsonreuters.uscl.ereader.core.book.domain",
            "com.thomsonreuters.uscl.ereader.request.domain"));
    }

    @Bean
    public BookDefinitionLockDao getDao() {
        return new BookDefinitionLockDaoImpl(sessionFactory());
    }

    @Bean
    public BookDefinitionService getBookDefinitionService() {
        final BookDefinitionDao dao = new BookDefinitionDaoImpl(sessionFactory());
        return new BookDefinitionServiceImpl(dao);
    }

    @Bean
    public PublisherCodeService getPublisherCodeService(final PublisherCodeDao dao) {
        return new PublisherCodeServiceImpl(dao);
    }

    @Bean
    public DocumentTypeCodeService getDocumnetTypeCodeService(final DocumentTypeCodeDao dao) {
        return new DocumentTypeCodeServiceImpl(dao);
    }
}
