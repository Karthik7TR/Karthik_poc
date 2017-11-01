package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
@EnableTransactionManagement
public class CodeServiceIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public CodeServiceIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setPackagesToScan("com.thomsonreuters.uscl.ereader"));
    }

    @Bean
    public CodeDao codeDao() {
        return new CodeDaoImpl(sessionFactory());
    }

    @Bean
    public CodeService codeService() {
        return new CodeServiceImpl(codeDao());
    }

    @Bean
    public BookDefinitionDao bookDefinitionDao() {
        return new BookDefinitionDaoImpl(sessionFactory());
    }

    @Bean
    public BookDefinitionService bookDefinitionService() {
        return new BookDefinitionServiceImpl(bookDefinitionDao());
    }
}
