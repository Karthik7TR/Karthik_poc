package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import java.io.File;
import java.util.Collection;
import java.util.Map.Entry;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

/**
 * Generates mapping file between anchors and pages where they are.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class InternalAnchorsStep extends XppTransformationStep
{
    @Value("${xpp.anchor.to.document.map.xsl}")
    private File transformToAnchorToDocumentIdMapXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(transformToAnchorToDocumentIdMapXsl).build();

        for (final Entry<String, Collection<File>> entry : fileSystem.getSectionBreaksFiles(this).entrySet())
        {
            final String materialNumber = entry.getKey();
            final TransformationCommand command =
                new TransformationCommandBuilder(transformer, fileSystem.getAnchorToDocumentIdMapFile(this, materialNumber))
                    .withInput(entry.getValue()).build();
            transformationService.transform(command);
        }
    }
}
