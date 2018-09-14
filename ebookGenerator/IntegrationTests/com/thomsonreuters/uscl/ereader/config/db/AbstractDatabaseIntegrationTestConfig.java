package com.thomsonreuters.uscl.ereader.config.db;

import java.util.Properties;
import java.util.function.Consumer;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.HSQLDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
    public DataSource jpaDataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.HSQL)
            .build();
    }

    @SneakyThrows
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
        sessionFactory.afterPropertiesSet();
        return sessionFactory.getObject();
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", HSQLDialect.class.getName());
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "create");
        final JpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(jpaVendorAdapter);
        factory.setPackagesToScan("com.thomsonreuters.uscl.ereader");
        factory.setJpaProperties(properties);
        factory.setDataSource(jpaDataSource());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        final HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory());
        return transactionManager;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager(final EntityManagerFactory factory) {
        final JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(factory);
        return txManager;
    }
}
