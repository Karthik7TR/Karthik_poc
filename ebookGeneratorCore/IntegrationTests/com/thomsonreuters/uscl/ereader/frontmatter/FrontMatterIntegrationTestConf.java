package com.thomsonreuters.uscl.ereader.frontmatter;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.dao.DocumentTypeCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.EBookAuditDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.PublisherCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditServiceImpl;
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
public class FrontMatterIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public FrontMatterIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setPackagesToScan(
            "com.thomsonreuters.uscl.ereader.core.book",
            "com.thomsonreuters.uscl.ereader.request.domain"));
    }

    @Bean
    public EbookAuditDao eBookAuditDao() {
        return new EBookAuditDaoImpl(sessionFactory());
    }

    @Bean
    public BookDefinitionDao bookDefinitionDao() {
        return new BookDefinitionDaoImpl(sessionFactory());
    }

    @Bean
    public EBookAuditService eBookAuditService() {
        return new EBookAuditServiceImpl(eBookAuditDao());
    }

    @Bean
    public BookDefinitionService bookDefinitionService() {
        return new BookDefinitionServiceImpl(bookDefinitionDao());
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
