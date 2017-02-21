package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import java.util.Date;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusService(StatsUpdateTypeEnum.INITIALIZE)
public class InitializePublishingStatusUpdateService extends BasePublishingStatusUpdateService
{
    @Resource(name = "eBookAuditService")
    private EBookAuditService eBookAuditService;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService#savePublishingStats(com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep, com.thomsonreuters.uscl.ereader.stats.PublishingStatus)
     */
    @Override
    public void savePublishingStats(final BookStepImpl step, final PublishingStatus publishStatus)
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
}
