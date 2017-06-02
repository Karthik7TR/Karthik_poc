package com.thomsonreuters.uscl.ereader.xpp.gather.docToImageMapping.step;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

/**
 * Generates mapping file between documents and containing images.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class DocToImageMappingStep extends XppTransformationStep
{
    @Value("${xpp.document.to.image.map.xsl}")
    private File transformToAnchorToDocumentIdMapXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(transformToAnchorToDocumentIdMapXsl).build();

        transformationService.transform(
            transformer,
            Arrays.asList(fileSystem.getHtmlPagesDirectory(this).listFiles(new FileFilter()
            {
                @Override
                public boolean accept(final File pathname)
                {
                    //TODO: temporary filter to make next steps work with old directory structure
                    return pathname.isFile();
                }
            })),
            fileSystem.getDocToImageMapFile(this));
    }

}
