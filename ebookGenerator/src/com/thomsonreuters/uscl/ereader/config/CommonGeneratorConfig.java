package com.thomsonreuters.uscl.ereader.config;

import org.apache.commons.net.ftp.FTPClient;
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
}
