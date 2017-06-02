package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import java.io.File;
import java.util.Collection;
import java.util.Map;

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
        final Transformer transformer = transformerBuilderFactory.create().withXsl(transformToHtmlXsl).build();
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getOriginalPageFiles(this).entrySet())
        {
            FileUtils.forceMkdir(fileSystem.getHtmlPagesDirectory(this, dir.getKey()));
            for (final File part : dir.getValue())
            {
                transformer.setParameter("fileBaseName", FilenameUtils.removeExtension(part.getName()));
                transformationService
                    .transform(transformer, part, fileSystem.getHtmlPageFile(this, dir.getKey(), part.getName()));
            }

            //TODO: temporary solution to make next steps work with old directory structure
            FileUtils.copyDirectory(fileSystem.getHtmlPagesDirectory(this, dir.getKey()), fileSystem.getHtmlPagesDirectory(this));
        }
    }
}
