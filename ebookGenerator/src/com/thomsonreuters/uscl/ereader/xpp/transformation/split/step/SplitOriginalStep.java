package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
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
    @Value("${xpp.move.multicolumns.up.xsl}")
    private File moveMultiColumnsUpXsl;
    @Value("${xpp.move.sectionbreaks.up.xsl}")
    private File moveSectionbreaksUpXsl;
    @Value("${xpp.split.original.xsl}")
    private File splitOriginalXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        moveMultiColumnsToTopLevel();
        moveSectionbreaksToTopLevel();
        splitByPages();
    }

    /**
     * TODO: unite functionality of moveMultiColumnsToTopLevel() and moveSectionbreaksToTopLevel() in java8
     */
    private void moveMultiColumnsToTopLevel() throws IOException
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(moveMultiColumnsUpXsl).build();
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getSectionBreaksFiles(this).entrySet())
        {
            FileUtils.forceMkdir(fileSystem.getMultiColumnsUpDirectory(this, dir.getKey()));
            for (final File file : dir.getValue())
            {
                final File multiColumnsUpFile = fileSystem.getMultiColumnsUpFile(this, dir.getKey(), file.getName());
                final TransformationCommand command =
                    new TransformationCommandBuilder(transformer, multiColumnsUpFile).withInput(file).build();
                transformationService.transform(command);
            }
        }
    }

    private void moveSectionbreaksToTopLevel() throws IOException
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(moveSectionbreaksUpXsl).build();
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getMultiColumnsUpFiles(this).entrySet())
        {
            FileUtils.forceMkdir(fileSystem.getSectionbreaksUpDirectory(this, dir.getKey()));
            for (final File file : dir.getValue())
            {
                final File sectionbreaksUpFile = fileSystem.getSectionbreaksUpFile(this, dir.getKey(), file.getName());
                final TransformationCommand command =
                    new TransformationCommandBuilder(transformer, sectionbreaksUpFile).withInput(file).build();
                transformationService.transform(command);
            }
        }
    }

    private void splitByPages() throws IOException
    {
        for (final Map.Entry<String, Collection<File>> entry : fileSystem.getSectionbreaksUpFiles(this).entrySet())
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
                final File originalPartsDirectory = fileSystem.getOriginalPartsDirectory(this, entry.getKey());
                final TransformationCommand command =
                    new TransformationCommandBuilder(transformer, originalPartsDirectory).withInput(file).build();
                transformationService.transform(command);
            }
        }
    }
}
