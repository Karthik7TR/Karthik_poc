package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
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
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.provider.TocGenerationStrategyProvider;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Extract TOC to intermediate format which is ready to use in title.xml
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ExtractTocStep extends XppTransformationStep
{
    private static Logger LOG = LogManager.getLogger(ExtractTocStep.class);

    @Value("${xpp.unite.tocs.xsl}")
    private File uniteTocsXsl;
    @Autowired
    private TocGenerationStrategyProvider tocGenerationStrategyProvider;

    //TODO: Two fields below should be removed when books will generate from bundles in XPP pathway.
    @Value("${xpp.toc.item.to.document.map.xsl}")
    private File buildTocItemToDocumentIdMapXsl;
    @Value("${xpp.extract.toc.xsl}")
    private File extractTocXsl;

    @Override
    public void executeTransformation() throws Exception
    {
        //TODO: this condition is temporary for backward compatibility
        /*
         * added: 06/02/2017
         * remove condition and else block when books will generate from bundles in XPP pathway.
         */
        if (isBundleHandlingInProgress())
        {
            final List<File> tocFiles = new ArrayList<>();
            for (final XppBundle bundle : getXppBundles())
            {
                generateBundleTocs(bundle, tocFiles);
            }

            final Transformer transformer = transformerBuilderFactory.create().withXsl(uniteTocsXsl).build();
            transformationService.transform(transformer, tocFiles, fileSystem.getTocFile(this));
        }
        else
        {
            buildTocItemToDocumentIdMap();
            buildIntermediateToc();
        }
    }

    private void generateBundleTocs(final XppBundle bundle, final List<File> tocFiles)
    {
        for (final String fileName : bundle.getOrderedFileList())
        {
            tocGenerationStrategyProvider
                .getTocGenerationStrategy(BundleFileType.getByFileName(fileName))
                .performTocGeneration(fileName, bundle, this);
            tocFiles.add(fileSystem.getBundlePartTocFile(fileName, bundle.getMaterialNumber(), this));
        }
    }

    //TODO: all code below should be removed when books will generate from bundles in XPP pathway.
    private boolean isBundleHandlingInProgress()
    {
        boolean result = false;
        for (final XppBundle bundle : getXppBundles())
        {
            if (checkBundleFilesExists(bundle))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean checkBundleFilesExists(final XppBundle bundle)
    {
        boolean result = false;
        for (final String fileName : bundle.getOrderedFileList())
        {
            if (isBundleFileExist(bundle, fileName))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean isBundleFileExist(final XppBundle bundle, final String fileName)
    {
        boolean result = false;
        try
        {
            BundleFileType.getByFileName(fileName);
            final File file = fileSystem.getOriginalFile(this, bundle.getMaterialNumber(), fileName);
            result = file.exists() && file.isFile();
        }
        catch (final Exception e)
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug(e.getMessage(), e);
            }
        }
        return result;
    }

    private void buildTocItemToDocumentIdMap()
    {
        final Transformer transformer =
            transformerBuilderFactory.create().withXsl(buildTocItemToDocumentIdMapXsl).build();

        transformationService.transform(
            transformer,
            Arrays.asList(fileSystem.getOriginalPagesDirectory(this).listFiles(new FileFilter()
            {
                @Override
                public boolean accept(final File pathname)
                {
                    //TODO: temporary filter to make next steps work with old directory structure
                    return pathname.isFile();
                }
            })),
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
