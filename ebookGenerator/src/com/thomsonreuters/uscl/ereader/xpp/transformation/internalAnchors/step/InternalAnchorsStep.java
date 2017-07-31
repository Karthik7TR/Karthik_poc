package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        final List<File> pageFiles = new ArrayList<>();
        for (final Collection<File> files : fileSystem.getOriginalPageFiles(this).values())
        {
            pageFiles.addAll(files);
        }
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, fileSystem.getAnchorToDocumentIdMapFile(this))
                .withInput(pageFiles).build();
        transformationService.transform(command);
    }
}
