package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.xpp.strategy.provider.BundleFileHandlingStrategyProvider;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step.strategy.PlaceXppMetadataStrategy;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.sonar.runner.commonsio.FileUtils;

/**
 *
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class PlaceXppMetadataStep extends XppTransformationStep
{
    @Resource(name = "placeXppMetadataStrategyProvider")
    private BundleFileHandlingStrategyProvider<PlaceXppMetadataStrategy> strategyProvider;

    @Override
    public void executeTransformation() throws Exception
    {
        final Map<String, Collection<File>> xppXmls = fileSystem.getOriginalMainAndFootnoteFiles(this);
        for (final Entry<String, Collection<File>> entry : xppXmls.entrySet())
        {
            handleBundleFiles(entry.getKey(), entry.getValue());
        }
    }

    private void handleBundleFiles(final String materialNumber, final Collection<File> files) throws IOException
    {
        fileSystem.getStructureWithMetadataBundleDirectory(this, materialNumber).mkdirs();
        for (final File file : files)
        {
            final String fileName = file.getName();
            final BundleFileType bundleFileType = BundleFileType.getByFileName(fileName);
            if (bundleFileType == BundleFileType.MAIN_CONTENT || fileName.endsWith(PartType.FOOTNOTE.getName()))
            {
                FileUtils.copyFile(file, fileSystem.getStructureWithMetadataFile(this, materialNumber, fileName));
            }
            else
            {
                strategyProvider.getStrategy(bundleFileType)
                    .performHandling(file, materialNumber, this);
            }
        }
    }
}
