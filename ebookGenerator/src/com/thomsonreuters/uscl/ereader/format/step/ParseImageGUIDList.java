package com.thomsonreuters.uscl.ereader.format.step;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.service.XMLImageParserService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This step generates the Image GUID list file that is used to retrieve the images referenced
 * by any documents within this eBook.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
public class ParseImageGUIDList extends AbstractSbTasklet {
    private PublishingStatsService publishingStatsService;

    private XMLImageParserService xmlImageParserService;

    public void setxmlImageParserService(final XMLImageParserService xmlImageParserService) {
        this.xmlImageParserService = xmlImageParserService;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final Long jobInstance =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        final String xmlDirectory = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR);
        final String imgGuidListFile =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_GUIDS_FILE);
        final String imgDocMapFile =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE);

        final int numDocsInTOC = getRequiredIntProperty(jobExecutionContext, JobExecutionKey.EBOOK_STATS_DOC_COUNT);

        final File xmlDir = new File(xmlDirectory);
        final File imgGuidList = new File(imgGuidListFile);
        final File imgDocMap = new File(imgDocMapFile);

        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobInstance);
        String publishingStatus = "Completed";

        try {
            final int numDocsParsed = xmlImageParserService.generateImageList(xmlDir, imgGuidList, imgDocMap);

            if (numDocsParsed != numDocsInTOC) {
                final String message = "The number of documents wrapped by the HTMLWrapper Service did "
                    + "not match the number of documents retrieved from the eBook TOC. Wrapped "
                    + numDocsParsed
                    + " documents while the eBook TOC had "
                    + numDocsInTOC
                    + " documents.";
                log.error(message);
                throw new EBookFormatException(message);
            }
        } catch (final Exception e) {
            publishingStatus = "Failed";
            throw e;
        } finally {
            jobstats.setPublishStatus("parseImageGuids : " + publishingStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }
}
