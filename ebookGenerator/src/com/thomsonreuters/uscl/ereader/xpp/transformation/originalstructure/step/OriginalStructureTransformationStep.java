package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import java.io.File;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class OriginalStructureTransformationStep extends BookStepImpl
{
    @Value("${xpp.sample.xpp.directory}")
    private File xppDirectory;
    @Value("${xpp.transform.to.original.xsl}")
    private File transformToOriginalXsl;
    @Value("${xpp.transform.to.footnotes.xsl}")
    private File transformToFootnotesXsl;

    @Resource(name = "transformerBuilderFactory")
    private TransformerBuilderFactory transformerBuilderFactory;
    @Resource(name = "xslTransformationService")
    private XslTransformationService transformationService;
    @Resource(name = "xppFormatFileSystem")
    private XppFormatFileSystem fileSystem;
    @Resource(name = "transformationUtil")
    private TransformationUtil transformationUtil;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        if (transformationUtil.shouldSkip(this))
        {
            return ExitStatus.COMPLETED;
        }

        FileUtils.forceMkdir(fileSystem.getOriginalDirectory(this));
        getOriginalXmls();
        getFootnotesXmls();
        return ExitStatus.COMPLETED;
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
