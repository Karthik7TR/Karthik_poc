package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.XMLPreprocessService;
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

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.VOL_ONE;

/**
 * This step transforms the Novus extracted XML documents by adding additional mark ups and content
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class PreprocessXML extends AbstractSbTasklet {
    //TODO: Use logger API to get Logger instance to job-specific appender.
    private static final Logger LOG = LogManager.getLogger(PreprocessXML.class);
    private XMLPreprocessService preprocessService;
    private PublishingStatsService publishingStatsService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobInstance jobInstance = getJobInstance(chunkContext);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final Long jobId = jobInstance.getId();

        final String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR);
        final String preprocessDirectory =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_PREPROCESS_DIR);

        final int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);

        final File xmlDir = new File(xmlDirectory);
        final File preprocessDir = new File(preprocessDirectory);

        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobId);
        String stepStatus = "Completed";

        Set<String> pageVolumes = new HashSet<>();

        try {
            final int numDocsTransformed = preprocessService.transformXML(
                xmlDir,
                preprocessDir,
                bookDefinition.isFinalStage(),
                bookDefinition.getDocumentCopyrights(),
                bookDefinition.getDocumentCurrencies(),
                pageVolumes);

            if (numDocsTransformed != numDocsInTOC) {
                final String message = "The number of documents preprocessed did not match the number "
                    + "of documents retrieved from the eBook TOC. Preprocessed "
                    + numDocsTransformed
                    + " documents while the eBook TOC had "
                    + numDocsInTOC
                    + " documents.";
                LOG.error(message);
                throw new EBookFormatException(message);
            }
        } catch (final Exception e) {
            stepStatus = "Failed";
            throw e;
        } finally {
            jobstats.setPublishStatus("formatPreprocessXML : " + stepStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        setPageVolumesSetProperty(pageVolumes, jobExecutionContext);

        return ExitStatus.COMPLETED;
    }

    private void setPageVolumesSetProperty(final Set<String> pageVolumes, final ExecutionContext jobExecutionContext) {
        pageVolumes.stream()
                .filter(vol -> !VOL_ONE.equals(vol))
                .findFirst()
                .ifPresent(vol ->
                    jobExecutionContext.put(JobExecutionKey.PAGE_VOLUMES_SET, Boolean.TRUE)
                );
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }

    @Required
    public void setPreprocessService(final XMLPreprocessService preprocessService) {
        this.preprocessService = preprocessService;
    }
}
