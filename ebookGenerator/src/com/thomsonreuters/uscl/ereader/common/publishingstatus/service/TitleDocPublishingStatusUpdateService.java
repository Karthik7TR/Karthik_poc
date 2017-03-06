package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusStrategy(StatsUpdateTypeEnum.TITLEDOC)
public class TitleDocPublishingStatusUpdateService extends BasePublishingStatusUpdateService<BookStep>
{
    @Resource(name = "generalPublishingStatusUpdateService")
    private PublishingStatusUpdateService<PublishingStatusUpdateStep> generalService;
    @Resource(name = "eBookAssemblyService")
    private EBookAssemblyService assemblyService;
    @Resource(name = "assembleFileSystem")
    private AssembleFileSystem fileSystem;

    @Override
    public void savePublishingStats(final BookStep step, final PublishingStatus publishStatus)
    {
        if (publishStatus.equals(PublishingStatus.COMPLETED))
        {
            final String documentsPath = fileSystem.getDocumentsDirectory(step).getAbsolutePath();
            final long titleDocumentCount = assemblyService.getDocumentCount(documentsPath);

            final PublishingStats jobstatsTitle = new PublishingStats();
            jobstatsTitle.setJobInstanceId(step.getJobInstanceId());
            jobstatsTitle.setTitleDocCount((int) titleDocumentCount);
            jobstatsTitle.setPublishStatus(getPublishStatusString(step, publishStatus));
            publishingStatsService.updatePublishingStats(jobstatsTitle, StatsUpdateTypeEnum.TITLEDOC);
        }
        else
        {
            generalService.savePublishingStats(step, publishStatus);
        }
    }
}
