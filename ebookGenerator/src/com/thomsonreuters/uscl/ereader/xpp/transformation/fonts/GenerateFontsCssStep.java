package com.thomsonreuters.uscl.ereader.xpp.transformation.fonts;

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
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class GenerateFontsCssStep extends XppTransformationStep
{

    @Value("${xpp.transform.fonts.css.xsl}")
    private File transformFontsCssXsl;

    @Resource(name = "xppGatherFileSystem")
    private XppGatherFileSystem xppGatherFileSystem;

    @Override
    public void executeTransformation() throws Exception
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(transformFontsCssXsl).build();
        final Map<String, Collection<File>> sourceXmls = xppGatherFileSystem.getXppSourceXmls(this);
        for (final Map.Entry<String, Collection<File>> xppDir : sourceXmls.entrySet())
        {
            final File bundleCssDir = fileSystem.getFontsCssDirectory(this, xppDir.getKey());
            bundleCssDir.mkdirs();
            for (final File xppFile : xppDir.getValue())
            {
                final File cssFile = fileSystem.getFontsCssFile(this, xppDir.getKey(), xppFile.getName());
                transformationService.transform(transformer, xppFile, cssFile);
            }
        }
    }
}
