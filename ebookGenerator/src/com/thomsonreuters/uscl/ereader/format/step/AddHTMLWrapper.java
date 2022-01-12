package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLWrapperService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step adds a static predefined HTML header and footer and any ProView specific document wrappers.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
public class AddHTMLWrapper extends AbstractSbTasklet {
    private HTMLWrapperService htmlWrapperService;

    private PublishingStatsService publishingStatsService;

    public void sethtmlWrapperService(final HTMLWrapperService htmlWrapperService) {
        this.htmlWrapperService = htmlWrapperService;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final String postTransformDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR);
        final String htmlDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_HTML_WRAPPER_DIR);
        final String docToTocFileName =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE);

        final int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);

        final JobInstance jobInstance = getJobInstance(chunkContext);
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
        final String titleId = bookDefinition.getTitleId();
        final boolean keyciteToplineFlag = bookDefinition.getKeyciteToplineFlag();

        final Long jobId = jobInstance.getId();

        final File postTransformDir = new File(postTransformDirectory);
        final File htmlDir = new File(htmlDirectory);
        final File docToTocFile = new File(docToTocFileName);

        String stepStatus = "Completed";
        int numDocsWrapped = -1;
        try {
            numDocsWrapped = htmlWrapperService
                .addHTMLWrappers(postTransformDir, htmlDir, docToTocFile, titleId, jobId, keyciteToplineFlag);

            if (numDocsWrapped != numDocsInTOC) {
                final String message = "The number of documents wrapped by the HTMLWrapper Service did "
                    + "not match the number of documents retrieved from the eBook TOC. Wrapped "
                    + numDocsWrapped
                    + " documents while the eBook TOC had "
                    + numDocsInTOC
                    + " documents.";
                log.error(message);
                throw new EBookFormatException(message);
            }
        } catch (final EBookFormatException e) {
            stepStatus = "Failed";
            throw e;
        } finally {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(jobId);
            jobstats.setFormatDocCount(numDocsWrapped);
            jobstats.setPublishStatus("formatAddHTMLWrapper : " + stepStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.FORMATDOC);
        }

        return ExitStatus.COMPLETED;
    }
}
