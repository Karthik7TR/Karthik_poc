package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusStrategy(StatsUpdateTypeEnum.ASSEMBLEDOC)
public class AssemblePublishingStatusUpdateService extends BasePublishingStatusUpdateService<BookStep> {
    @Resource(name = "eBookAssemblyService")
    private EBookAssemblyService assemblyService;
    @Resource(name = "generalPublishingStatusUpdateService")
    private PublishingStatusUpdateService<PublishingStatusUpdateStep> generalService;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem fileSystem;

    @Override
    public void savePublishingStats(final BookStep step, final PublishingStatus publishStatus) {
        if (publishStatus.equals(PublishingStatus.COMPLETED)) {
            final String documentsPath = fileSystem.getDocumentsDirectory(step).getAbsolutePath();
            final String assetsPath = fileSystem.getAssetsDirectory(step).getAbsolutePath();

            final long largestDocument = assemblyService.getLargestContent(documentsPath, ".html");
            final long largestPdf = assemblyService.getLargestContent(assetsPath, ".pdf");
            final long largestImage = assemblyService.getLargestContent(assetsPath, ".png,.jpeg,.gif");
            final long bookSizeInBytes = fileSystem.getAssembledBookFile(step).length();

            final PublishingStats jobstatsFormat = new PublishingStats();
            jobstatsFormat.setJobInstanceId(step.getJobInstanceId());
            jobstatsFormat.setLargestDocSize(largestDocument);
            jobstatsFormat.setLargestImageSize(largestImage);
            jobstatsFormat.setLargestPdfSize(largestPdf);
            jobstatsFormat.setBookSize(bookSizeInBytes);
            jobstatsFormat.setPublishStatus(getPublishStatusString(step, publishStatus));
            publishingStatsService.updatePublishingStats(jobstatsFormat, StatsUpdateTypeEnum.ASSEMBLEDOC);
        } else {
            generalService.savePublishingStats(step, publishStatus);
        }
    }
}
