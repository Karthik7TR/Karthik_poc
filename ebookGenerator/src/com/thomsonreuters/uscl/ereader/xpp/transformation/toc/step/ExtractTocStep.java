package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.provider.TocGenerationStrategyProvider;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Extract TOC to intermediate format which is ready to use in title.xml
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ExtractTocStep extends XppTransformationStep
{
    @Value("${xpp.unite.tocs.xsl}")
    private File uniteTocsXsl;
    @Autowired
    private TocGenerationStrategyProvider tocGenerationStrategyProvider;

    @Override
    public void executeTransformation() throws Exception
    {
        final List<File> tocFiles = new ArrayList<>();
        for (final XppBundle bundle : getXppBundles())
        {
            generateBundleTocs(bundle, tocFiles);
        }

        final Transformer transformer = transformerBuilderFactory.create().withXsl(uniteTocsXsl).build();
        transformationService.transform(transformer, tocFiles, fileSystem.getTocFile(this));
    }

    private void generateBundleTocs(final XppBundle bundle, final List<File> tocFiles)
    {
        for (final String fileName : bundle.getOrderedFileList())
        {
            tocGenerationStrategyProvider.getTocGenerationStrategy(BundleFileType.getByFileName(fileName))
                .performTocGeneration(fileName, bundle, this);
            tocFiles.add(fileSystem.getBundlePartTocFile(fileName, bundle.getMaterialNumber(), this));
        }
    }
}
