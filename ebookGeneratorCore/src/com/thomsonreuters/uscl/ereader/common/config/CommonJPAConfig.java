package com.thomsonreuters.uscl.ereader.common.config;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.dao.EBookAuditDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.core.book.domain.VersionIsbn;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.statecode.StateCodeDaoImpl;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobCleanupDao;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobCleanupDaoImpl;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDao;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobRequestDaoImpl;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDao;
import com.thomsonreuters.uscl.ereader.core.outage.dao.OutageDaoImpl;
import com.thomsonreuters.uscl.ereader.core.outage.domain.OutageType;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.proviewaudit.dao.ProviewAuditDao;
import com.thomsonreuters.uscl.ereader.proviewaudit.dao.ProviewAuditDaoImpl;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponentHistory;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDaoImpl;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreference;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public abstract class CommonJPAConfig {
    @Bean
    @Primary
    public DataSource dataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        dsLookup.setResourceRef(true);
        return dsLookup.getDataSource("jdbc/DataSource");
    }

    @Bean
    public DataSource jpaDataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        dsLookup.setResourceRef(true);
        return dsLookup.getDataSource("jdbc/DataSourceJPA");
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") final DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory) {
        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager(final EntityManagerFactory factory) {
        final JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(factory);
        return txManager;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(
        @Value("${hibernate.dialect}") final String hibernateDialectProperty,
        @Value("${hibernate.show.sql}") final String showSqlProperty,
        @Value("${hibernate.cache.provider.class}") final String cacheProviderClassProperty,
        @Qualifier("jpaDataSource") final DataSource dataSource) {
        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", hibernateDialectProperty);
        properties.setProperty("hibernate.show_sql", showSqlProperty);
        properties.setProperty("hibernate.cache.provider_class", cacheProviderClassProperty);
        final JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(jpaVendorAdapter);
        factory.setPackagesToScan("com.thomsonreuters.uscl.ereader");
        factory.setJpaProperties(properties);
        factory.setDataSource(dataSource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public SessionFactory sessionFactory(
        @Value("${hibernate.dialect}") final String hibernateDialectProperty,
        @Value("${hibernate.show.sql}") final String showSqlProperty,
        @Value("${hibernate.cache.provider.class}") final String cacheProviderClassProperty,
        @Qualifier("dataSource") final DataSource dataSource) throws IOException {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", hibernateDialectProperty);
        properties.setProperty("hibernate.show_sql", showSqlProperty);
        properties.setProperty("hibernate.cache.provider_class", cacheProviderClassProperty);
        sessionFactory.setHibernateProperties(properties);

        final List<Class<?>> annotatedClasses = new ArrayList<>(
            Arrays.asList(
                AppParameter.class,
                PlannedOutage.class,
                OutageType.class,
                BookDefinition.class,
                CombinedBookDefinition.class,
                CombinedBookDefinitionSource.class,
                Author.class,
                VersionIsbn.class,
                DocumentTypeCode.class,
                EbookAudit.class,
                EbookName.class,
                JurisTypeCode.class,
                KeywordTypeCode.class,
                KeywordTypeValue.class,
                PublisherCode.class,
                PublishingStats.class,
                PubTypeCode.class,
                StateCode.class,
                JobRequest.class,
                FrontMatterPage.class,
                FrontMatterSection.class,
                FrontMatterPdf.class,
                ExcludeDocument.class,
                RenameTocEntry.class,
                TableViewer.class,
                UserPreference.class,
                ProviewAudit.class,
                DocumentCopyright.class,
                DocumentCurrency.class,
                NortFileLocation.class,
                SplitDocument.class,
                SplitNodeInfo.class,
                PilotBook.class,
                PrintComponent.class,
                PrintComponentHistory.class,
                XppBundleArchive.class));
        addAdditionalEntities(annotatedClasses);
        sessionFactory.setAnnotatedClasses(annotatedClasses.toArray(new Class<?>[annotatedClasses.size()]));

        sessionFactory.afterPropertiesSet();
        return sessionFactory.getObject();
    }

    protected abstract void addAdditionalEntities(List<Class<?>> annotatedClasses);

    /*
     * Repository beans for eBookManager and eBookGenerator
     */

    @Bean
    public BookDefinitionDao bookDefinitionDao(final SessionFactory sessionFactory) {
        return new BookDefinitionDaoImpl(sessionFactory);
    }

    @Bean
    public StateCodeDao stateCodeDao(final SessionFactory sessionFactory) {
        return new StateCodeDaoImpl(sessionFactory);
    }


    @Bean
    public PublishingStatsDao publishingStatsDao(final SessionFactory sessionFactory) {
        return new PublishingStatsDaoImpl(sessionFactory);
    }

    @Bean
    public JobRequestDao jobRequestDao(final SessionFactory sessionFactory) {
        return new JobRequestDaoImpl(sessionFactory);
    }

    @Bean
    public EbookAuditDao eBookAuditDAO(final SessionFactory sessionFactory) {
        return new EBookAuditDaoImpl(sessionFactory);
    }

    @Bean
    public ProviewAuditDao proviewAuditDao(final SessionFactory sessionFactory) {
        return new ProviewAuditDaoImpl(sessionFactory);
    }

    @Bean
    public JobCleanupDao jobCleanupDao(final SessionFactory sessionFactory) {
        return new JobCleanupDaoImpl(sessionFactory);
    }

    @Bean
    public OutageDao outageDao(final SessionFactory sessionFactory) {
        return new OutageDaoImpl(sessionFactory);
    }

}
