package com.thomsonreuters.uscl.ereader.common.publishingstatus.service;

import java.util.Date;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;

@SavePublishingStatusStrategy(StatsUpdateTypeEnum.INITIALIZE)
public class InitializePublishingStatusUpdateService extends BasePublishingStatusUpdateService<BookStep>
{
    @Resource(name = "eBookAuditService")
    private EBookAuditService eBookAuditService;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.common.publishingstatus.service.PublishingStatusUpdateService#savePublishingStats(com.thomsonreuters.uscl.ereader.common.publishingstatus.step.PublishingStatusUpdateStep, com.thomsonreuters.uscl.ereader.stats.PublishingStatus)
     */
    @Override
    public void savePublishingStats(final BookStep step, final PublishingStatus publishStatus)
    {
        final Date rightNow = publishingStatsService.getSysDate();
        final Long ebookDefId = step.getBookDefinitionId();

        final Long auditId = eBookAuditService.findEbookAuditByEbookDefId(ebookDefId);
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(auditId);

        final PublishingStats pubStats = new PublishingStats();
        pubStats.setEbookDefId(ebookDefId);
        pubStats.setAudit(audit);
        pubStats.setBookVersionSubmitted(step.getBookVersionString());
        pubStats.setJobHostName(step.getHostName());
        pubStats.setJobInstanceId(step.getJobInstanceId());
        pubStats.setJobSubmitterName(step.getUserName());
        pubStats.setJobSubmitTimestamp(step.getSubmitTimestamp());
        pubStats.setPublishStatus(getPublishStatusString(step, publishStatus));
        pubStats.setPublishStartTimestamp(rightNow);
        publishingStatsService.savePublishingStats(pubStats);
    }
}
