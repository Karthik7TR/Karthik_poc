package com.thomsonreuters.uscl.ereader.config;

import com.thomsonreuters.uscl.ereader.core.service.local.DevEnvironmentValidator;
import com.thomsonreuters.uscl.ereader.core.service.local.DevEnvironmentValidatorImpl;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CommonGeneratorConfig {
    @Bean
    @Scope("prototype")
    public FTPClient ftpClient() {
        return new FTPClient();
    }

    @Bean
    public DevEnvironmentValidator devEnvironmentValidator(@Value("${environment}") final String environmentName) {
        return new DevEnvironmentValidatorImpl(environmentName);
    }
}
