package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
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
    @Value("${xpp.transform.to.original.xsl}")
    private File transformToOriginalXsl;
    @Value("${xpp.transform.to.footnotes.xsl}")
    private File transformToFootnotesXsl;
    @Resource(name = "xppGatherFileSystem")
    private XppGatherFileSystem xppGatherFileSystem;

    @Override
    public void executeTransformation() throws Exception
    {
        FileUtils.forceMkdir(fileSystem.getOriginalDirectory(this));

        final Map<String, Collection<File>> xppXmls = xppGatherFileSystem.getXppSourceXmls(this);
        for (final Map.Entry<String, Collection<File>> xppDir : xppXmls.entrySet())
        {
            final File bundleOriginalDir = fileSystem.getOriginalBundleDirectory(this, xppDir.getKey());
            bundleOriginalDir.mkdirs();
        }

        getOriginalXmls(xppXmls);
        getFootnotesXmls(xppXmls);
    }

    private void getOriginalXmls(final Map<String, Collection<File>> xppXmls)
    {
        final Transformer transformerToOriginal =
            transformerBuilderFactory.create().withXsl(transformToOriginalXsl).build();

        for (final Map.Entry<String, Collection<File>> xppDir : xppXmls.entrySet())
        {
            for (final File xppFile : xppDir.getValue())
            {
                final File originalFile = fileSystem.getOriginalFile(this, xppDir.getKey(), xppFile.getName());
                transformationService.transform(transformerToOriginal, xppFile, originalFile);

                //TODO: temporary solution to make next steps work with old directory structure
                transformationService.transform(transformerToOriginal, xppFile, fileSystem.getOriginalFile(this, xppFile.getName()));
            }
        }
    }

    private void getFootnotesXmls(final Map<String, Collection<File>> xppXmls)
    {
        final Transformer transformerToFootnotes =
            transformerBuilderFactory.create().withXsl(transformToFootnotesXsl).build();

        for (final Map.Entry<String, Collection<File>> xppDir : xppXmls.entrySet())
        {
            for (final File xppFile : xppDir.getValue())
            {
                final File footnotesFile = fileSystem.getFootnotesFile(this, xppDir.getKey(), xppFile.getName());
                transformationService.transform(transformerToFootnotes, xppFile, footnotesFile);

                //TODO: temporary solution to make next steps work with old directory structure
                transformationService.transform(transformerToFootnotes, xppFile, fileSystem.getFootnotesFile(this, xppFile.getName()));
            }
        }
    }
}
