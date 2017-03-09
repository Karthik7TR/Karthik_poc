package com.thomsonreuters.uscl.ereader.xpp.transformation.split.step;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SplitOriginalStep extends BookStepImpl
{
    private static final Logger LOG = LogManager.getLogger(SplitOriginalStep.class);

    @Value("${xpp.sample.xpp.directory}")
    private File xppDirectory;
    @Value("${xpp.move.pagebreakes.up.xsl}")
    private File movePagebreakesUpXsl;
    @Value("${xpp.split.original.xsl}")
    private File splitOriginalXsl;

    @Resource(name = "transformerBuilderFactory")
    private TransformerBuilderFactory transformerBuilderFactory;
    @Resource(name = "xslTransformationService")
    private XslTransformationService transformationService;
    @Resource(name = "xppFormatFileSystem")
    private XppFormatFileSystem fileSystem;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        //TODO: remove after transformer will start to get real input data
        if (!xppDirectory.exists())
        {
            LOG.debug(String.format("%s skipped, because sample xppTemp directory not found", getStepName()));
            return ExitStatus.COMPLETED;
        }

        movePageBreakesToTopLevel();
        splitByPages();
        return ExitStatus.COMPLETED;
    }

    private void movePageBreakesToTopLevel() throws IOException
    {
        FileUtils.forceMkdir(fileSystem.getPagebreakesUpDirectory(this));
        final Transformer transformer = transformerBuilderFactory.create().withXsl(movePagebreakesUpXsl).build();
        for (final File original : fileSystem.getOriginalDirectory(this).listFiles())
        {
            transformationService
                .transform(transformer, original, fileSystem.getPagebreakesUpFile(this, original.getName()));
        }
    }

    private void splitByPages() throws IOException
    {
        FileUtils.forceMkdir(fileSystem.getOriginalPartsDirectory(this));
        final Transformer transformer = transformerBuilderFactory.create().withXsl(splitOriginalXsl).build();
        for (final File original : fileSystem.getPagebreakesUpDirectory(this).listFiles())
        {
            final String fileBaseName = FilenameUtils.removeExtension(original.getName());
            transformer.setParameter("fileBaseName", fileBaseName);
            transformationService.transform(transformer, original, fileSystem.getOriginalPartsDirectory(this));
        }
    }
}
