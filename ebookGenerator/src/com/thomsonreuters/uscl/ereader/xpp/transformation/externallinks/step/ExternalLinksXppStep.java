package com.thomsonreuters.uscl.ereader.xpp.transformation.externallinks.step;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery.CiteQueryMapper;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery.CiteQueryMapperResponse;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ExternalLinksXppStep extends XppTransformationStep {
    @Value("${xpp.external.links.xsl}")
    private File externalLinksXsl;

    @Autowired
    private XppFormatFileSystem fileSystem;

    @Autowired
    private CiteQueryMapper citeQueryMapper;

    @Override
    public void executeTransformation() throws Exception {
        final Transformer citeQueryTransformer = transformerBuilderFactory.create().withXsl(externalLinksXsl).build();
        fileSystem.getHtmlPageFiles(this).forEach((materialNumber, files) -> {
            for (final File file : files) {
                final CiteQueryMapperResponse response = citeQueryMapper.createMappingFile(file, materialNumber, this);
                final String fileName = file.getName();
                handleFailedCiteQueryTags(response.getFailedTags(), materialNumber, fileName);
                citeQueryTransformer.setParameter("mappingFile", response.getMapFilePath());
                final File externalLinksFile = fileSystem.getExternalLinksFile(this, materialNumber, fileName);
                final TransformationCommand command =
                    new TransformationCommandBuilder(citeQueryTransformer, externalLinksFile).withInput(file).build();
                transformationService.transform(command);
            }
        });
    }

    private void handleFailedCiteQueryTags(final Collection<String> failedTags, final String materialNumber, final String name) {
        try {
            if (!failedTags.isEmpty()) {
                final File outputFile = fileSystem.getFile(
                    this, XppFormatFileSystemDir.FAILED_CITE_QUERY_TAGS, materialNumber, String.join(".", name, "txt"));
                FileUtils.writeLines(outputFile, failedTags);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Cannot create file with failed tags", e);
        }
    }
}
