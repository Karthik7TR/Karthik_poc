package com.thomsonreuters.uscl.ereader.gather.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveServiceLookup;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.thomsonreuters.uscl.ereader.stats.PublishingStatus.COMPLETED;
import static com.thomsonreuters.uscl.ereader.stats.PublishingStatus.FAILED;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrepareDocsTask extends AbstractSbTasklet {
    private final PublishingStatsService publishingStatsService;
    private final RetrieveServiceLookup retrieveServiceLookup;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
            throws Exception {
        PublishingStatus publishStatus = COMPLETED;
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
        final File tocFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE));
        final File docsGuidsFile =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE));
        final Long jobInstanceId = getJobInstance(chunkContext).getId();
        final PublishingStats publishingStats = PublishingStats.initStats(jobInstanceId);
        try {
            GatherResponse gatherResponse = retrieveServiceLookup.getRetrieveService(bookDefinition.getSourceType())
                    .retrieveDocsAndMetadata(bookDefinition, tocFile, docsGuidsFile, chunkContext);
            publishingStatsService.addDocsAndMetadataStats(publishingStats, gatherResponse);
            jobExecutionContext.putInt(JobExecutionKey.EBOOK_STATS_DOC_COUNT, gatherResponse.getDocCount());
        } catch (final Exception e) {
            publishStatus = FAILED;
            log.error(e.getMessage());
            throw e;
        } finally {
            publishingStats.setPublishStatus("getDocAndMetadata : " + publishStatus);
            publishingStatsService.updatePublishingStats(publishingStats, StatsUpdateTypeEnum.GATHERDOC);
        }
        return ExitStatus.COMPLETED;
    }
}
