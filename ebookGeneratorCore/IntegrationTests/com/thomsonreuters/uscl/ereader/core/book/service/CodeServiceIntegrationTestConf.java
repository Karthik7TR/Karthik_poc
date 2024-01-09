package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.KeywordTypeCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.KeywordTypeValueDao;
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
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "jpaTransactionManager")
public class CodeServiceIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public CodeServiceIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setPackagesToScan("com.thomsonreuters.uscl.ereader"));
    }

    @Bean
    public KeywordTypeCodeSevice keywordTypeCodeSevice(
        final KeywordTypeCodeDao keywordTypeCodeDao,
        final BookDao bookDao) {
        return new KeywordTypeCodeServiceImpl(keywordTypeCodeDao, bookDao);
    }

    @Bean
    public KeywordTypeValueService keywordTypeValueSevice(
        final KeywordTypeValueDao keywordTypeValueDao,
        final KeywordTypeCodeDao keywordTypeCodeDao,
        final BookDao bookDao) {
        return new KeywordTypeValueServiceImpl(keywordTypeValueDao, keywordTypeCodeDao, bookDao);
    }

    @Bean
    public CodeService codeService(CodeDao codeDao) {
        return new CodeServiceImpl(codeDao);
    }
}
