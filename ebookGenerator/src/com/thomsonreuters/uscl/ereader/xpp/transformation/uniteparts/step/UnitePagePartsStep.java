package com.thomsonreuters.uscl.ereader.xpp.transformation.uniteparts.step;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
        final Transformer transformer = transformerBuilderFactory.create().withXsl(unitePagePartsXsl).build();

        for (final Map.Entry<String, Collection<File>> dir : fileSystem.getOriginalMainAndFootnoteFiles(this).entrySet())
        {
            FileUtils.forceMkdir(fileSystem.getOriginalPagesDirectory(this, dir.getKey()));
            for (final File originalFile : dir.getValue())
            {
                if (originalFile.getName().endsWith(".main"))
                {
                    createPage(transformer, dir.getKey(), originalFile.getName());
                }
            }

            //TODO: temporary solution to make next steps work with old directory structure
            FileUtils.copyDirectory(fileSystem.getOriginalPagesDirectory(this, dir.getKey()), fileSystem.getOriginalPagesDirectory(this));
        }
    }

    private void createPage(final Transformer transformer, final String materialNumber, final String fileName)
    {
        final int numberOfPages = getNumberOfPagesForGivenBasefile(materialNumber, fileName);
        for (int i = 1; i <= numberOfPages; i++)
        {
            final File mainPart = fileSystem.getOriginalPartsFile(this, materialNumber, fileName, i, PartType.MAIN);
            //TODO: return back when split by structure for footnotes is ready
            //final File footnotesPart = fileSystem.getOriginalPartsFile(this, materialNumber, fileName, i, PartType.FOOTNOTE);
            final List<File> files = asList(mainPart);

            transformationService.transform(transformer, files, fileSystem.getOriginalPageFile(this, materialNumber, fileName, i));
        }
    }

    private int getNumberOfPagesForGivenBasefile(final String materialNumber, final String fileName)
    {
        final String baseFileName = FilenameUtils.removeExtension(fileName);
        final Pattern pattern = Pattern.compile(baseFileName + "_[0-9]+_.+\\.part");
        return
            fileSystem.getOriginalPartsDirectory(this, materialNumber).listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(final File dir, final String name)
                {
                    return pattern.matcher(name).matches();
                }
            }).length / PartType.values().length;
    }
}
