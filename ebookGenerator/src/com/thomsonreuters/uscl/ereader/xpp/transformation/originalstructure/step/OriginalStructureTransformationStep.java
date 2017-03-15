package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Creates original XMLs from XPP XMLs
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class OriginalStructureTransformationStep extends XppTransformationStep
{
    @Value("${xpp.sample.xpp.directory}")
    private File xppDirectory;
    @Value("${xpp.transform.to.original.xsl}")
    private File transformToOriginalXsl;
    @Value("${xpp.transform.to.footnotes.xsl}")
    private File transformToFootnotesXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        FileUtils.forceMkdir(fileSystem.getOriginalDirectory(this));
        getOriginalXmls();
        getFootnotesXmls();
    }

    private void getOriginalXmls()
    {
        final Transformer transformerToOriginal =
            transformerBuilderFactory.create().withXsl(transformToOriginalXsl).build();
        for (final File xppFile : xppDirectory.listFiles())
        {
            final File originalFile = fileSystem.getOriginalFile(this, xppFile.getName());
            transformationService.transform(transformerToOriginal, xppFile, originalFile);
        }
    }

    private void getFootnotesXmls()
    {
        final Transformer transformerToFootnotes =
            transformerBuilderFactory.create().withXsl(transformToFootnotesXsl).build();
        for (final File xppFile : xppDirectory.listFiles())
        {
            final File footnotesFile = fileSystem.getFootnotesFile(this, xppFile.getName());
            transformationService.transform(transformerToFootnotes, xppFile, footnotesFile);
        }
    }
}
