package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Unite different parts of original XML page to one page XML
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class UnitePagePartsStep extends XppTransformationStep
{
    @Value("${xpp.unite.page.parts.xsl}")
    private File unitePagePartsXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        FileUtils.forceMkdir(fileSystem.getOriginalPagesDirectory(this));
        final Transformer transformer = transformerBuilderFactory.create().withXsl(unitePagePartsXsl).build();
        for (final File originalFile : fileSystem.getOriginalFiles(this))
        {
            createPage(transformer, originalFile.getName());
        }
    }

    private void createPage(final Transformer transformer, final String fileName)
    {
        final int numberOfPages =
            fileSystem.getOriginalPartsDirectory(this).listFiles().length / PartType.values().length;
        for (int i = 1; i <= numberOfPages; i++)
        {
            final File mainPart = fileSystem.getOriginalPartsFile(this, fileName, i, PartType.MAIN);
            final File footnotesPart = fileSystem.getOriginalPartsFile(this, fileName, i, PartType.FOOTNOTE);
            final List<File> files = asList(mainPart, footnotesPart);

            transformationService.transform(transformer, files, fileSystem.getOriginalPageFile(this, fileName, i));
        }
    }
}
