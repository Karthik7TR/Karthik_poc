package com.thomsonreuters.uscl.ereader.core.book.statecode;

import com.thomsonreuters.uscl.ereader.config.AbstractDatabaseIntegrationTestConfig;
import com.thomsonreuters.uscl.ereader.sql.SQLDual;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Profile("IntegrationTests")
@EnableTransactionManagement
public class StateCodeServiceIntegrationTestConf extends AbstractDatabaseIntegrationTestConfig {
    public StateCodeServiceIntegrationTestConf() {
        super(sessionFactory -> sessionFactory.setAnnotatedClasses(new Class<?>[]{SQLDual.class, StateCode.class}));
    }

    @Bean
    public StateCodeDao stateCodeDao() {
        return new StateCodeDaoImpl(sessionFactory());
    }

    @Bean
    public StateCodeService stateCodeService() {
        return new StateCodeServiceImpl(stateCodeDao());
    }
}
