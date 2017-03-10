package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

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
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.TransformationUtil;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class TransformationToHtmlStep extends BookStepImpl
{
    @Value("${xpp.transform.to.html.xsl}")
    private File transformToHtmlXsl;
    @Resource(name = "transformerBuilderFactory")
    private TransformerBuilderFactory transformerBuilderFactory;
    @Resource(name = "xslTransformationService")
    private XslTransformationService transformationService;
    @Resource(name = "transformationUtil")
    private TransformationUtil transformationUtil;
    @Resource(name = "xppFormatFileSystem")
    private XppFormatFileSystem fileSystem;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        if (transformationUtil.shouldSkip(this))
        {
            return ExitStatus.COMPLETED;
        }

        transformToHtml();
        return ExitStatus.COMPLETED;
    }

    private void transformToHtml() throws IOException
    {
        FileUtils.forceMkdir(fileSystem.getToHtmlDirectory(this));
        final Transformer transformer = transformerBuilderFactory.create().withXsl(transformToHtmlXsl).build();
        for (final File part : fileSystem.getOriginalPartsDirectory(this).listFiles())
        {
            transformationService
                .transform(transformer, part, fileSystem.getToHtmlFile(this, part.getName()));
        }
    }
}
