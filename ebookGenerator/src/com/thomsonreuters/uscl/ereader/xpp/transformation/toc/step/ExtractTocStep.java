package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import java.io.File;
import java.util.Arrays;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

/**
 * Extract TOC to intermediate format which is ready to use in title.xml
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ExtractTocStep extends XppTransformationStep
{
    @Value("${xpp.toc.item.to.document.map.xsl}")
    private File buildTocItemToDocumentIdMapXsl;

    @Value("${xpp.extract.toc.xsl}")
    private File extractTocXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        buildTocItemToDocumentIdMap();
        buildIntermediateToc();
    }

    private void buildTocItemToDocumentIdMap()
    {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(buildTocItemToDocumentIdMapXsl).build();

        transformationService.transform(
            transformer,
            Arrays.asList(fileSystem.getOriginalPagesDirectory(this).listFiles()),
            fileSystem.getTocItemToDocumentIdMapFile(this));
    }

    private void buildIntermediateToc()
    {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(extractTocXsl).build();
        transformer.setParameter("mapFilePath", fileSystem.getTocItemToDocumentIdMapFile(this).getAbsolutePath().replace("\\", "/"));

        transformationService.transform(
            transformer,
            fileSystem.getOriginalFiles(this),
            fileSystem.getTocFile(this));
    }
}
