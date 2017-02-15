package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.EBookAssemblyService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * Step responsible for assembling an eBook.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class AssembleEbook extends AbstractSbTasklet
{
    //TODO: Use logger API to get Logger instance to job-specific appender.
    private static final Logger LOG = LogManager.getLogger(AssembleEbook.class);
    private EBookAssemblyService eBookAssemblyService;
    private PublishingStatsService publishingStatsService;
    private DocMetadataService docMetadataService;

    public DocMetadataService getDocMetadataService()
    {
        return docMetadataService;
    }

    public void setDocMetadataService(final DocMetadataService docMetadataService)
    {
        this.docMetadataService = docMetadataService;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final Long jobInstanceId =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        String eBookDirectoryPath = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_DIRECTORY);
        final String eBookFilePath = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FILE);

        File eBookDirectory = new File(eBookDirectoryPath);
        final File eBookFile = new File(eBookFilePath);

        final long startTime = System.currentTimeMillis();
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        try
        {
            if (!bookDefinition.isSplitBook())
            {
                eBookAssemblyService.assembleEBook(eBookDirectory, eBookFile);
            }
            else
            {
                final List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(jobInstanceId);
                for (String splitTitleId : splitTitles)
                {
                    splitTitleId = StringUtils.substringAfterLast(splitTitleId, "/");
                    final File splitEbookFile = new File(
                        getRequiredStringProperty(jobExecutionContext, JobExecutionKey.WORK_DIRECTORY),
                        splitTitleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
                    eBookDirectoryPath = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.ASSEMBLE_DIR)
                        + "/"
                        + splitTitleId;
                    eBookDirectory = new File(eBookDirectoryPath);
                    if (eBookDirectory == null || !eBookDirectory.isDirectory())
                    {
                        throw new IOException(
                            "eBookDirectory must not be null and must be a directory." + eBookDirectoryPath);
                    }
                    eBookAssemblyService.assembleEBook(eBookDirectory, splitEbookFile);
                }
            }
        }
        catch (final Exception e)
        {
            final PublishingStats jobstatsFormat = new PublishingStats();
            jobstatsFormat.setJobInstanceId(jobInstanceId);
            jobstatsFormat.setPublishStatus("assembleEBook : Failed");
            throw (e);
        }

        final long gzipLength = eBookFile.length();
        updateAssembleEbookStats(gzipLength, jobInstanceId, eBookDirectoryPath);
        updateTitleDocumentCount(jobInstanceId, eBookDirectoryPath);

        final long endTime = System.currentTimeMillis();
        final long elapsedTime = endTime - startTime;

        //TODO: Consider defining the time spent in assembly as a JODA-Time interval.
        LOG.debug("Assembled eBook in " + elapsedTime + " milliseconds");

        return ExitStatus.COMPLETED;
    }

    private void updateAssembleEbookStats(final long gzipeSize, final Long jobId, final String eBookDirectoryPath)
    {
        final File docFile = new File(eBookDirectoryPath);
        final File finalDocDir = new File(docFile, "documents");
        final File finalPdfImageDir = new File(docFile, "assets");

        final String documentsPath = finalDocDir.getAbsolutePath();
        final String pdfImagePath = finalPdfImageDir.getAbsolutePath();

        final long largestDocuent = eBookAssemblyService.getLargestContent(documentsPath, ".html");
        final long largestPdf = eBookAssemblyService.getLargestContent(pdfImagePath, ".pdf");
        final long largestImage = eBookAssemblyService.getLargestContent(pdfImagePath, ".png,.jpeg,.gif");

        final PublishingStats jobstatsFormat = new PublishingStats();
        jobstatsFormat.setJobInstanceId(jobId);
        jobstatsFormat.setLargestDocSize(largestDocuent);
        jobstatsFormat.setLargestImageSize(largestImage);
        jobstatsFormat.setLargestPdfSize(largestPdf);
        jobstatsFormat.setBookSize(gzipeSize);
        jobstatsFormat.setPublishStatus("assembleEBook : Complete");

        publishingStatsService.updatePublishingStats(jobstatsFormat, StatsUpdateTypeEnum.ASSEMBLEDOC);
    }

    private void updateTitleDocumentCount(final Long jobId, final String eBookDirectoryPath)
    {
        final File docFile = new File(eBookDirectoryPath);
        final File finalDocDir = new File(docFile, "documents");
        final String documentsPath = finalDocDir.getAbsolutePath();

        final double titleDocumentCount = eBookAssemblyService.getDocumentCount(documentsPath);
        final PublishingStats jobstatsTitle = new PublishingStats();
        jobstatsTitle.setJobInstanceId(jobId);
        jobstatsTitle.setTitleDocCount((int) titleDocumentCount);
        jobstatsTitle.setPublishStatus("assembleEBook : Complete");
        publishingStatsService.updatePublishingStats(jobstatsTitle, StatsUpdateTypeEnum.TITLEDOC);
    }

    @Required
    public void seteBookAssemblyService(final EBookAssemblyService eBookAssemblyService)
    {
        this.eBookAssemblyService = eBookAssemblyService;
    }

    @Required
    public void setPublishingStatsService(

        final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }
}
