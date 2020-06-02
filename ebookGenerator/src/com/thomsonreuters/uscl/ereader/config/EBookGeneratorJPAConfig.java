package com.thomsonreuters.uscl.ereader.config;

import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.common.config.CommonJPAConfig;
import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;
import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDaoImpl;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.metadata.dao.DocMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.dao.DocMetadataDaoImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.dao.PaceMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.dao.PaceMetadataDaoImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.support.lob.OracleLobHandler;
import org.springframework.jdbc.support.nativejdbc.CommonsDbcpNativeJdbcExtractor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.thomsonreuters.uscl.ereader",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "jpaTransactionManager"
)
@EnableJpaAuditing
@EnableTransactionManagement
public class EBookGeneratorJPAConfig extends CommonJPAConfig {
    @Bean
    public ImageDao imageDao(final SessionFactory sessionFactory) {
        return new ImageDaoImpl(sessionFactory);
    }

    @Bean
    public DocMetadataDao docMetaDataDao(final SessionFactory sessionFactory) {
        return new DocMetadataDaoImpl(sessionFactory);
    }

    @Bean
    public PaceMetadataDao paceMetaDataDao(final SessionFactory sessionFactory) {
        return new PaceMetadataDaoImpl(sessionFactory);
    }

    @Bean
    public OracleLobHandler oracleLobHandler() {
        final OracleLobHandler handler = new OracleLobHandler();
        handler.setNativeJdbcExtractor(new CommonsDbcpNativeJdbcExtractor());
        return handler;
    }

    @Override
    protected void addAdditionalEntities(final List<Class<?>> annotatedClasses) {
        annotatedClasses.addAll(Arrays.asList(
            XSLTMapperEntity.class, ImageMetadataEntity.class, DocMetadata.class, PaceMetadata.class, CanadianTopicCode.class, CanadianDigest.class));
    }
}
