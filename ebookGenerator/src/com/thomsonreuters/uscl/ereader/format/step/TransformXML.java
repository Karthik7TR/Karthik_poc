package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.TransformerService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step transforms the Novus extracted XML documents into HTML.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TransformXML extends AbstractSbTasklet
{
    //TODO: Use logger API to get Logger instance to job-specific appender.
    private static final Logger LOG = LogManager.getLogger(TransformXML.class);
    private TransformerService transformerService;
    private PublishingStatsService publishingStatsService;

    public void settransformerService(final TransformerService transformerService)
    {
        this.transformerService = transformerService;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobInstance jobInstance = getJobInstance(chunkContext);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
        final String titleId = bookDefinition.getTitleId();

        final Long jobId = jobInstance.getId();

        final String preprocessDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_PREPROCESS_DIR);
        final String metadataDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR);
        final String transformDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_TRANSFORMED_DIR);
        final String imgMetadataDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_IMAGE_METADATA_DIR);

        final int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);

        final File preprocessDir = new File(preprocessDirectory);
        final File metadataDir = new File(metadataDirectory);
        final File transformDir = new File(transformDirectory);
        final File imgMetadataDir = new File(imgMetadataDirectory);

        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String stepStatus = "Completed";

        final File staticContentDir =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.STATIC_CONTENT_DIR));

        try
        {
            final long startTime = System.currentTimeMillis();
            final int numDocsTransformed = transformerService.transformXMLDocuments(
                preprocessDir,
                metadataDir,
                imgMetadataDir,
                transformDir,
                jobId,
                bookDefinition,
                staticContentDir);
            final long endTime = System.currentTimeMillis();
            final long elapsedTime = endTime - startTime;

            if (numDocsTransformed != numDocsInTOC)
            {
                final String message = "The number of documents transformed did not match the number "
                    + "of documents retrieved from the eBook TOC. Transformed "
                    + numDocsTransformed
                    + " documents while the eBook TOC had "
                    + numDocsInTOC
                    + " documents.";
                LOG.error(message);
                throw new EBookFormatException(message);
            }

            LOG.debug("Transformed " + numDocsTransformed + " XML files in " + elapsedTime + " milliseconds");
        }
        catch (final Exception e)
        {
            stepStatus = "Failed";
            throw e;
        }
        finally
        {
            jobstats.setPublishStatus("formatTransformXML : " + stepStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }
}
