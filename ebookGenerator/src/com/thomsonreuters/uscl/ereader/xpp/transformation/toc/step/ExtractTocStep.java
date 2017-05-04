package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

/**
 * Extract TOC to intermediate format which is ready to use in title.xml
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ExtractTocStep extends XppTransformationStep
{
    private static Logger LOG = LogManager.getLogger(ExtractTocStep.class);

    @Value("${xpp.toc.item.to.document.map.xsl}")
    private File buildTocItemToDocumentIdMapXsl;

    @Value("${xpp.extract.toc.xsl}")
    private File extractTocXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        buildTocItemToDocumentIdMap();
        buildIntermediateToc();
    }

    private void buildTocItemToDocumentIdMap()
    {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(buildTocItemToDocumentIdMapXsl).build();

        transformationService.transform(
            transformer,
            Arrays.asList(fileSystem.getOriginalPagesDirectory(this).listFiles()),
            fileSystem.getTocItemToDocumentIdMapFile(this));
    }

    private void buildIntermediateToc()
    {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(extractTocXsl).build();
        transformer.setParameter("mapFilePath", fileSystem.getTocItemToDocumentIdMapFile(this).getAbsolutePath().replace("\\", "/"));

        final List<InputStream> inputStreams = asInputStreamsWithTitlebreaks(fileSystem.getOriginalFiles(this));

        transformationService.transform(
            transformer,
            inputStreams,
            fileSystem.getOriginalDirectory(this).getAbsolutePath(),
            fileSystem.getTocFile(this));
    }

    private List<InputStream> asInputStreamsWithTitlebreaks(final Collection<File> originalFiles)
    {
        try
        {
            if (originalFiles.size() == 1)
            {
                return Collections.singletonList((InputStream) new FileInputStream(originalFiles.iterator().next()));
            }
            else if (originalFiles.size() > 1)
            {
                return combineToStreamsWithTitlebreaks(originalFiles);
            }
        }
        catch (final FileNotFoundException e)
        {
            LOG.error(e);
        }
        throw new RuntimeException("No original files found in " + fileSystem.getOriginalDirectory(this).getAbsolutePath());
    }

    private List<InputStream> combineToStreamsWithTitlebreaks(final Collection<File> originalFiles) throws FileNotFoundException
    {
        //TODO: reorder streams according to order which assigned in PRINT_COMPONENTS table
        final List<InputStream> inputStreams = new ArrayList<>();
        int i = 1;
        for (final File inputFile : originalFiles)
        {
            inputStreams.add(new ByteArrayInputStream(String.format("<titlebreak>eBook %s of %s</titlebreak>", i++, originalFiles.size()).getBytes()));
            inputStreams.add(new FileInputStream(inputFile));
        }
        return inputStreams;
    }
}
