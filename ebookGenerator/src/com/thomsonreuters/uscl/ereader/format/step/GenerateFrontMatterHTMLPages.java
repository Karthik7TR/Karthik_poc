package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.service.CreateFrontMatterService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step adds a static predefined HTML header and footer and any ProView specific document wrappers.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class GenerateFrontMatterHTMLPages extends AbstractSbTasklet {
    //TODO: Use logger API to get Logger instance to job-specific appender.
    private static final Logger LOG = LogManager.getLogger(GenerateFrontMatterHTMLPages.class);
    private CreateFrontMatterService frontMatterService;

    private PublishingStatsService publishingStatsService;

    public void setfrontMatterService(final CreateFrontMatterService frontMatterService) {
        this.frontMatterService = frontMatterService;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final String frontMatterTargetDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR);

        final File frontMatterTargetDir = new File(frontMatterTargetDirectory);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final Long jobInstance =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        final long startTime = System.currentTimeMillis();
        String publishStatus = "generateFrontMatterHTML : Completed";

        try {
            frontMatterService.generateAllFrontMatterPages(frontMatterTargetDir, bookDefinition);
        } catch (final Exception e) {
            publishStatus = "generateFrontMatterHTML : Failed";
            throw (e);
        } finally {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(jobInstance);
            jobstats.setPublishStatus(publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        final long endTime = System.currentTimeMillis();
        final long elapsedTime = endTime - startTime;

        //TODO: Improve metrics
        LOG.debug("Generated all the Front Matter HTML pages in " + elapsedTime + " milliseconds");

        return ExitStatus.COMPLETED;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }
}
