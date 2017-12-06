package com.thomsonreuters.uscl.ereader.mgr.config;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.thomsonreuters.uscl.ereader",
    entityManagerFactoryRef = "entityManagerFactory"
)
@EnableTransactionManagement
public class EBookManagerJPAConfig {
    @Bean
    @Autowired
    public EntityManagerFactory entityManagerFactory(
            @Value("${hibernate.dialect}") final String hibernateDialectProperty,
            @Value("${hibernate.show.sql}") final String showSqlProperty,
            @Value("${hibernate.cache.provider.class}") final String cacheProviderClassProperty) {
        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", hibernateDialectProperty);
        properties.setProperty("hibernate.show_sql", showSqlProperty);
        properties.setProperty("hibernate.cache.provider_class", cacheProviderClassProperty);
        final JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(jpaVendorAdapter);
        factory.setPackagesToScan("com.thomsonreuters.uscl.ereader");
        factory.setJpaProperties(properties);
        factory.setDataSource(dataSource());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory) {
        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    @Bean
    public DataSource dataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        dsLookup.setResourceRef(true);
        return dsLookup.getDataSource("jdbc/DataSource");
    }
}
