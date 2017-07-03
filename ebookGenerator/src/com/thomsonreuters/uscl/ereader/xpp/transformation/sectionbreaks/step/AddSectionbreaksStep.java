package com.thomsonreuters.uscl.ereader.xpp.transformation.sectionbreaks.step;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Add sectionbreak tags to original XML
 * TODO: unite this step with PlaceXppMetadataStep
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class AddSectionbreaksStep extends XppTransformationStep
{
    @Value("${xpp.add.sectionbreaks.original.xsl}")
    private File addSectionbreaksToOriginalXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(addSectionbreaksToOriginalXsl).build();
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getStructureWithMetadataFiles(this).entrySet())
        {
            FileUtils.forceMkdir(fileSystem.getSectionbreaksDirectory(this, dir.getKey()));
            for (final File file : dir.getValue())
            {
                final String fileName = file.getName();
                final String materialNumber = dir.getKey();
                final BundleFileType bundleFileType = BundleFileType.getByFileName(fileName);
                if (bundleFileType == BundleFileType.MAIN_CONTENT || fileName.endsWith(".footnote"))
                {
                    transformationService.transform(transformer, file, fileSystem.getSectionbreaksFile(this, materialNumber, fileName));
                }
                else
                {
                    FileUtils.copyFile(file, fileSystem.getSectionbreaksFile(this, materialNumber, fileName));
                }
            }
        }
    }
}
