package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import java.util.Date;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class PublishingStatusUpdateServiceImpl implements PublishingStatusUpdateService<BookStep>
{
    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;
    @Resource(name = "eBookAuditService")
    private EBookAuditService eBookAuditService;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService#savePublishingStats(com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep, com.thomsonreuters.uscl.ereader.stats.PublishingStatus)
     */
    @Override
    public void savePublishingStats(final BookStep step, final PublishingStatus publishStatus)
    {
        if (step.isInitialStep())
        {
            createPublishingStats(step, publishStatus);
        }
        else
        {
            updatePublishingStats(step, publishStatus);
        }
    }

    private void createPublishingStats(final BookStep step, final PublishingStatus publishStatus)
    {
        final Date rightNow = new Date();
        final Long ebookDefId = step.getBookDefinitionId();

        final Long auditId = eBookAuditService.findEbookAuditByEbookDefId(ebookDefId);
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(auditId);

        final PublishingStats pubStats = new PublishingStats();
        pubStats.setEbookDefId(ebookDefId);
        pubStats.setAudit(audit);
        pubStats.setBookVersionSubmitted(step.getBookVersion());
        pubStats.setJobHostName(step.getHostName());
        pubStats.setJobInstanceId(step.getJobInstanceId());
        pubStats.setJobSubmitterName(step.getUserName());
        pubStats.setJobSubmitTimestamp(step.getSubmitTimestamp());
        pubStats.setPublishStatus(getPublishStatusString(step, publishStatus));
        pubStats.setPublishStartTimestamp(rightNow);
        pubStats.setLastUpdated(rightNow);
        publishingStatsService.savePublishingStats(pubStats);
    }

    private void updatePublishingStats(final BookStep step, final PublishingStatus publishStatus)
    {
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(step.getJobInstanceId());
        jobstats.setPublishStatus(getPublishStatusString(step, publishStatus));
        publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
    }

    private String getPublishStatusString(final BookStep step, final PublishingStatus publishStatus)
    {
        return String.format("%s : %s", step.getStepName(), publishStatus);
    }
}
