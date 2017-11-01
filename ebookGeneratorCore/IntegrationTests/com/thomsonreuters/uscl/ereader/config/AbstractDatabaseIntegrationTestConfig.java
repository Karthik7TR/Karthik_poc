package com.thomsonreuters.uscl.ereader.config;

import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.dialect.HSQLDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class AbstractDatabaseIntegrationTestConfig {
    private final Consumer<LocalSessionFactoryBean> entitiesDefinition;

    protected AbstractDatabaseIntegrationTestConfig(
        final Consumer<LocalSessionFactoryBean> entitiesDefinition) {
        this.entitiesDefinition = entitiesDefinition;
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .build();
    }

    @Bean
    public SessionFactory sessionFactory() {
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());

        final Properties properties = new Properties();
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.dialect", HSQLDialect.class.getName());
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        sessionFactory.setHibernateProperties(properties);

        entitiesDefinition.accept(sessionFactory);

        try {
            sessionFactory.afterPropertiesSet();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return sessionFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory());
        return transactionManager;
    }
}
