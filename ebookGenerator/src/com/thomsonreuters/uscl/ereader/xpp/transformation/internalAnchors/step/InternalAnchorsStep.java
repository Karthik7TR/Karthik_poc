package com.thomsonreuters.uscl.ereader.xpp.transformation.internalAnchors.step;

import java.io.File;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Value;

/**
 * Generates mapping file between anchors and pages where they are.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class InternalAnchorsStep extends BookStepImpl
{
    @Value("${xpp.anchor.to.document.map.xsl}")
    private File transformToAnchorToDocumentIdMapXsl;
    @Resource(name = "transformerBuilderFactory")
    private TransformerBuilderFactory transformerBuilderFactory;
    @Resource(name = "xslTransformationService")
    private XslTransformationService transformationService;
    @Resource(name = "transformationUtil")
    private TransformationUtil transformationUtil;
    @Resource(name = "xppFormatFileSystem")
    private XppFormatFileSystem fileSystem;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        if (transformationUtil.shouldSkip(this))
        {
            return ExitStatus.COMPLETED;
        }

        generateAnchorToDocumentMap();
        return ExitStatus.COMPLETED;
    }

    private void generateAnchorToDocumentMap()
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(transformToAnchorToDocumentIdMapXsl).build();

        transformationService
            .transform(transformer, Arrays.asList(fileSystem.getToHtmlDirectory(this).listFiles()), fileSystem.getAnchorToDocumentIdMapFile(this));
    }
}
