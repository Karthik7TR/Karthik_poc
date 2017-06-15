package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import java.io.File;
import java.io.IOException;
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
 * Splits original XMLs by pages
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SplitOriginalStep extends XppTransformationStep
{
    @Value("${xpp.move.pagebreakes.up.xsl}")
    private File movePagebreakesUpXsl;
    @Value("${xpp.split.original.xsl}")
    private File splitOriginalXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        movePageBreakesToTopLevel();
        splitByPages();
    }

    private void movePageBreakesToTopLevel() throws IOException
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(movePagebreakesUpXsl).build();
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getSectionBreaksFiles(this).entrySet())
        {
            FileUtils.forceMkdir(fileSystem.getPagebreakesUpDirectory(this, dir.getKey()));
            for (final File file : dir.getValue())
            {
                transformationService.transform(transformer, file, fileSystem.getPagebreakesUpFile(this, dir.getKey(), file.getName()));
            }
        }
    }

    private void splitByPages() throws IOException
    {
        for (final Map.Entry<String, Collection<File>> entry : fileSystem.getPagebreakesUpFiles(this).entrySet())
        {
            final Transformer transformer = transformerBuilderFactory.create().withXsl(splitOriginalXsl).build();
            FileUtils.forceMkdir(fileSystem.getOriginalPartsDirectory(this, entry.getKey()));
            for (final File file : entry.getValue())
            {
                final String fileName = file.getName();
                final String fileBaseName = FilenameUtils.removeExtension(fileName);
                final String fileType = FilenameUtils.getExtension(fileName);

                transformer.setParameter("fileBaseName", fileBaseName);
                transformer.setParameter("fileType", fileType);
                transformationService.transform(transformer, file, fileSystem.getOriginalPartsDirectory(this, entry.getKey()));
            }

            //TODO: temporary solution to make next steps work with old directory structure
            FileUtils.copyDirectory(fileSystem.getOriginalPartsDirectory(this, entry.getKey()), fileSystem.getOriginalPartsDirectory(this));
        }
    }
}
