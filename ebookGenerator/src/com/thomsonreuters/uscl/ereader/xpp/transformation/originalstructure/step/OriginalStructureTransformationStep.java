package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import java.io.File;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class OriginalStructureTransformationStep extends BookStepImpl
{
    private static final Logger LOG = LogManager.getLogger(OriginalStructureTransformationStep.class);

    @Value("${xpp.sample.xpp.directory}")
    private File xppDirectory;
    @Value("${xpp.transform.to.original.xsl}")
    private File transformToOriginalXsl;

    @Resource(name = "transformerBuilder")
    private TransformerBuilder transformerBuilder;
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
            LOG.debug(
                String.format(
                    "%s skipped, because sample xppTemp directory not found",
                    getStepName()));
            return ExitStatus.COMPLETED;
        }

        fileSystem.getOriginalDirectory(this).mkdirs();
        final Transformer transformer = transformerBuilder.create(transformToOriginalXsl).build();
        for (final File xppFile : xppDirectory.listFiles())
        {
            transformXppFile(transformer, xppFile);
        }
        return ExitStatus.COMPLETED;
    }

    private void transformXppFile(final Transformer transformer, final File xppFile)
    {
        final File originalFile = fileSystem.getOriginalFile(this, xppFile.getName());
        transformationService.transform(transformer, xppFile, originalFile);
    }
}
