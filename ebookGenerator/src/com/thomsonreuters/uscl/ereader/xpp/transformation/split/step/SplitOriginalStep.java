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
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Splits original XMLs by pages
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SplitOriginalStep extends XppTransformationStep {
    static final XppFormatFileSystemDir INPUT_DIR_1 = XppFormatFileSystemDir.CROSS_PAGE_FOOTNOTES;
    static final XppFormatFileSystemDir OUTPUT_DIR_1 = XppFormatFileSystemDir.MULTICOLUMNS_UP_DIR;
    static final XppFormatFileSystemDir INPUT_DIR_2 = OUTPUT_DIR_1;
    static final XppFormatFileSystemDir OUTPUT_DIR_2 = XppFormatFileSystemDir.SECTIONBREAKS_UP_DIR;
    static final XppFormatFileSystemDir INPUT_DIR_3 = OUTPUT_DIR_2;
    static final XppFormatFileSystemDir OUTPUT_DIR_3 = XppFormatFileSystemDir.ORIGINAL_PARTS_DIR;

    @Value("${xpp.move.multicolumns.up.xsl}")
    private File moveMultiColumnsUpXsl;
    @Value("${xpp.move.sectionbreaks.up.xsl}")
    private File moveSectionbreaksUpXsl;
    @Value("${xpp.split.original.xsl}")
    private File splitOriginalXsl;

    @Override
    public void executeTransformation() throws Exception {
        moveMultiColumnsToTopLevel();
        moveSectionbreaksToTopLevel();
        splitByPages();
    }

    /**
     * TODO: unite functionality of moveMultiColumnsToTopLevel() and moveSectionbreaksToTopLevel() in java8
     */
    private void moveMultiColumnsToTopLevel() throws IOException {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(moveMultiColumnsUpXsl).build();
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getFiles(this, INPUT_DIR_1).entrySet()) {
            FileUtils.forceMkdir(fileSystem.getDirectory(this, OUTPUT_DIR_1, dir.getKey()));
            for (final File file : dir.getValue()) {
                final File multiColumnsUpFile = fileSystem.getFile(this, OUTPUT_DIR_1, dir.getKey(), file.getName());
                final TransformationCommand command =
                    new TransformationCommandBuilder(transformer, multiColumnsUpFile).withInput(file).build();
                transformationService.transform(command);
            }
        }
    }

    private void moveSectionbreaksToTopLevel() throws IOException {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(moveSectionbreaksUpXsl).build();
        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getFiles(this, INPUT_DIR_2).entrySet()) {
            FileUtils.forceMkdir(fileSystem.getDirectory(this, OUTPUT_DIR_2, dir.getKey()));
            for (final File file : dir.getValue()) {
                final File sectionbreaksUpFile = fileSystem.getFile(this, OUTPUT_DIR_2, dir.getKey(), file.getName());
                final TransformationCommand command =
                    new TransformationCommandBuilder(transformer, sectionbreaksUpFile).withInput(file).build();
                transformationService.transform(command);
            }
        }
    }

    private void splitByPages() throws IOException {
        for (final Map.Entry<String, Collection<File>> entry : fileSystem.getFiles(this, INPUT_DIR_3).entrySet()) {
            final Transformer transformer = transformerBuilderFactory.create().withXsl(splitOriginalXsl).build();
            final File originalPartsDirectory = fileSystem.getDirectory(this, OUTPUT_DIR_3, entry.getKey());
            FileUtils.forceMkdir(originalPartsDirectory);
            for (final File file : entry.getValue()) {
                final String fileName = file.getName();
                final String fileBaseName = FilenameUtils.removeExtension(fileName);
                final String fileType = FilenameUtils.getExtension(fileName);

                transformer.setParameter("fileBaseName", fileBaseName);
                transformer.setParameter("fileType", fileType);
                final TransformationCommand command =
                    new TransformationCommandBuilder(transformer, originalPartsDirectory).withInput(file).build();
                transformationService.transform(command);
            }
        }
    }
}
