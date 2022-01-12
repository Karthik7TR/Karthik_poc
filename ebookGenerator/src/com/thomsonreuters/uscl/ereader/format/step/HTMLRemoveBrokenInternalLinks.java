package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.util.Collection;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.HTMLRemoveBrokenInternalLinksService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step transforms the HTML generated by the transformation process into ProView acceptable HTML.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
@Slf4j
public class HTMLRemoveBrokenInternalLinks extends AbstractSbTasklet {
    private HTMLRemoveBrokenInternalLinksService transformerUnlinkService;
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobInstance jobInstance = getJobInstance(chunkContext);
        final JobParameters jobParams = getJobParameters(chunkContext);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final String titleId = bookDefinition.getTitleId();
        final Long jobId = jobInstance.getId();

        final String transformDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_CREATED_DIR);
        final String postTransformDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR);

        final int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);
        final String username = jobParams.getString(JobParameterKey.USER_NAME);
        final String envName = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);
        final Collection<InternetAddress> emailRecipients = emailUtil.getEmailRecipientsByUsername(username);

        final File transformDir = new File(transformDirectory);
        final File postTransformDir = new File(postTransformDirectory);

        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String stepStatus = "Completed";
        try {
            final int numDocsTransformed = transformerUnlinkService
                .transformHTML(transformDir, postTransformDir, titleId, jobId, envName, emailRecipients);

            if (numDocsTransformed != numDocsInTOC) {
                final String message = "The number of post transformed documents did not match the number "
                    + "of documents retrieved from the eBook TOC. Transformed "
                    + numDocsTransformed
                    + " documents while the eBook TOC had "
                    + numDocsInTOC
                    + " documents.";
                log.error(message);
                throw new EBookFormatException(message);
            }
        } catch (final Exception e) {
            stepStatus = "Failed";
            throw e;
        } finally {
            jobstats.setPublishStatus("formatHTMLRemoveBrokenInternalLinks : " + stepStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    @Required
    public void setTransformerUnlinkService(final HTMLRemoveBrokenInternalLinksService transformerUnlinkService) {
        this.transformerUnlinkService = transformerUnlinkService;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }
}
