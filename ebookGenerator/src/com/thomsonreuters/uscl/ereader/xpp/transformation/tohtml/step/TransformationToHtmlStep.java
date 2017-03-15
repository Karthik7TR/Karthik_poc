package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Creates HTML pages using pages in original XML markup
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class TransformationToHtmlStep extends XppTransformationStep
{
    @Value("${xpp.transform.to.html.xsl}")
    private File transformToHtmlXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        FileUtils.forceMkdir(fileSystem.getToHtmlDirectory(this));
        final Transformer transformer = transformerBuilderFactory.create().withXsl(transformToHtmlXsl).build();
        for (final File part : fileSystem.getOriginalPartsDirectory(this).listFiles())
        {
            transformer.setParameter("fileBaseName", FilenameUtils.removeExtension(part.getName()));
            transformationService
                .transform(transformer, part, fileSystem.getToHtmlFile(this, part.getName()));
        }
    }
}
