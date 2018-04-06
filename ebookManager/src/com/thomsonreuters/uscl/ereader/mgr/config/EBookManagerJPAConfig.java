package com.thomsonreuters.uscl.ereader.mgr.config;

import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.common.config.CommonJPAConfig;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionLockDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionLockDaoImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobDaoImpl;
import com.thomsonreuters.uscl.ereader.core.job.domain.BatchJobExecution;
import com.thomsonreuters.uscl.ereader.core.job.domain.BatchStepExecution;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobExecutionEntity;
import com.thomsonreuters.uscl.ereader.core.job.service.JobNameProvider;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDao;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDaoImpl;
import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListDao;
import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListDaoImpl;
import com.thomsonreuters.uscl.ereader.mgr.library.dao.LibraryListRowMapper;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.web.service.author.AuthorService;
import com.thomsonreuters.uscl.ereader.stats.util.PublishingStatsUtil;
import com.thomsonreuters.uscl.ereader.support.dao.SupportPageLinkDao;
import com.thomsonreuters.uscl.ereader.support.dao.SupportPageLinkDaoImpl;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.thomsonreuters.uscl.ereader",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "jpaTransactionManager"
)
@EnableTransactionManagement
public class EBookManagerJPAConfig extends CommonJPAConfig {
    @Bean
    public JobDao jobDao(final JdbcTemplate jdbcTemplate) {
        final JobDaoImpl jobDao = new JobDaoImpl();
        jobDao.setJdbcTemplate(jdbcTemplate);
        return jobDao;
    }

    @Bean
    public LibraryListDao libraryListDao(final RowMapper<LibraryList> libraryListMapper, final JdbcTemplate jdbcTemplate) {
        return new LibraryListDaoImpl(libraryListMapper, jdbcTemplate);
    }

    @Bean
    public RowMapper<LibraryList> libraryListMapper(final AuthorService authorService) {
        return new LibraryListRowMapper(authorService);
    }

    @Bean
    public BookDefinitionLockDao bookDefintionLockDao(final SessionFactory sessionFactory) {
        return new BookDefinitionLockDaoImpl(sessionFactory);
    }

    @Bean
    public SupportPageLinkDao supportPageLinkDao(final SessionFactory sessionFactory) {
        return new SupportPageLinkDaoImpl(sessionFactory);
    }

    @Bean
    public ManagerDao managerDao(final SessionFactory sessionFactory, final JdbcTemplate jdbcTemplate,
                                 final JobExplorer jobExplorer, final JobNameProvider jobNameProvider,
                                 final PublishingStatsUtil publishingStatsUtil) {
        final ManagerDaoImpl managerDao = new ManagerDaoImpl();
        managerDao.setSessionFactory(sessionFactory);
        managerDao.setJdbcTemplate(jdbcTemplate);
        managerDao.setJobExplorer(jobExplorer);
        managerDao.setJobNameProvider(jobNameProvider);
        managerDao.setPublishingStatsUtil(publishingStatsUtil);
        return managerDao;
    }

    @Override
    protected void addAdditionalEntities(final List<Class<?>> annotatedClasses) {
        annotatedClasses.addAll(Arrays.asList(BookDefinitionLock.class, JobExecutionEntity.class,
            BatchJobExecution.class, BatchStepExecution.class, SupportPageLink.class));
    }
}
