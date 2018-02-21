package com.thomsonreuters.uscl.ereader.core.book.dao;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceImpl;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
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
    public CodeService getCodeService() {
        final CodeDao dao = new CodeDaoImpl(sessionFactory());
        return new CodeServiceImpl(dao);
    }
}
