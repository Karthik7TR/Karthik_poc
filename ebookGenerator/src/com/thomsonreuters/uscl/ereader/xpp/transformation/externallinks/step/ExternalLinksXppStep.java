package com.thomsonreuters.uscl.ereader.xpp.transformation.externallinks.step;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery.CiteQueryMapper;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
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
        final Map<String, Collection<File>> htmlFilesMap = fileSystem.getHtmlPageFiles(this);
        for (final Map.Entry<String, Collection<File>> entry : htmlFilesMap.entrySet()) {
            for (final File file : entry.getValue()) {
                citeQueryTransformer
                    .setParameter("mappingFile", citeQueryMapper.createMappingFile(file, entry.getKey(), this));
                final File externalLinksFile = fileSystem.getExternalLinksFile(this, entry.getKey(), file.getName());
                final TransformationCommand command =
                    new TransformationCommandBuilder(citeQueryTransformer, externalLinksFile).withInput(file).build();
                transformationService.transform(command);
            }
        }
    }
}
