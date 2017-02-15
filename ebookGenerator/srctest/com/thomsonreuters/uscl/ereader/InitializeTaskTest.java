package com.thomsonreuters.uscl.ereader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

public final class InitializeTaskTest
{
    private static final Long JOB_ID = System.currentTimeMillis();
    private static final String TITLE_ID = "JunitTestTitleId";
    private static final String DATE_STAMP = new SimpleDateFormat("yyyyMMdd").format(new Date());
    private InitializeTask task;
    private StepContribution stepContrib;
    private ChunkContext chunkContext;
    private StepContext stepContext;
    private StepExecution stepExecution;
    private JobExecution jobExecution;
    private JobInstance jobInstance;
    private ExecutionContext jobExecutionContext;
    private File tempRootDir;
    private PublishingStatsService publishingStatsService;
    private EBookAuditService eBookAuditService;

    @Before
    public void setUp()
    {
        stepContrib = EasyMock.createMock(StepContribution.class);
        chunkContext = EasyMock.createMock(ChunkContext.class);
        stepContext = EasyMock.createMock(StepContext.class);
        stepExecution = EasyMock.createMock(StepExecution.class);
        jobExecution = EasyMock.createMock(JobExecution.class);
        jobInstance = EasyMock.createMock(JobInstance.class);
        jobExecutionContext = new ExecutionContext();
        task = new InitializeTask();
        tempRootDir = new File(System.getProperty("java.io.tmpdir"));
        task.setRootWorkDirectory(tempRootDir);
        publishingStatsService = EasyMock.createMock(PublishingStatsService.class);
        eBookAuditService = EasyMock.createMock(EBookAuditService.class);
    }

    @Test
    public void dummyTest()
    {
        //Intentionally left blank
    }

    @Ignore
    @Test
    public void testExecuteStep() throws Exception
    {
        final Map<String, JobParameter> paramMap = new HashMap<>();
        final JobParameters jobParams = new JobParameters(paramMap);
        EasyMock.expect(chunkContext.getStepContext()).andReturn(stepContext);
        EasyMock.expect(stepContext.getStepExecution()).andReturn(stepExecution);
        EasyMock.expect(stepExecution.getJobExecution()).andReturn(jobExecution);
        EasyMock.expect(jobExecution.getExecutionContext()).andReturn(jobExecutionContext);

        final PublishingStats pubStats = new PublishingStats();
        final EbookAudit audit = new EbookAudit();
        audit.setAuditId(Long.valueOf(1));
        pubStats.setAudit(audit);
        pubStats.setBookVersionSubmitted("0");
        pubStats.setJobHostName("hostname");
        pubStats.setJobInstanceId(Long.valueOf(1));
        pubStats.setJobInstanceId(Long.valueOf(1));
        pubStats.setJobSubmitterName("test_username");
        pubStats.setJobSubmitTimestamp(new Date());

        publishingStatsService.savePublishingStats(pubStats);

        EasyMock.expect(jobExecution.getJobInstance()).andReturn(jobInstance);
        EasyMock.expect(jobExecution.getJobParameters()).andReturn(jobParams);
        EasyMock.expect(jobInstance.getId()).andReturn(JOB_ID);
        EasyMock.replay(chunkContext);
        EasyMock.replay(stepContext);
        EasyMock.replay(stepExecution);
        EasyMock.replay(jobExecution);
        EasyMock.replay(jobInstance);
        EasyMock.replay(publishingStatsService);
        EasyMock.replay(eBookAuditService);
        File expectedWorkDirectory = null;
        try
        {
            final ExitStatus transition = task.executeStep(stepContrib, chunkContext);

            // Verify the root directory for this ebook
            final String dynamicPath = "data/" + String.format("%s/%s/%d", DATE_STAMP, TITLE_ID, JOB_ID);

            expectedWorkDirectory = new File(tempRootDir, dynamicPath);
            final File expectedEbookDirectory = new File(expectedWorkDirectory, "Assemble" + File.separatorChar + TITLE_ID);
            final File expectedEbookFile = new File(expectedWorkDirectory, TITLE_ID + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);

            final File actualEbookDirectory = new File(jobExecutionContext.getString(JobExecutionKey.EBOOK_DIRECTORY));
            final File actualEbookFile = new File(jobExecutionContext.getString(JobExecutionKey.EBOOK_FILE));
            final File actualImagesRootDirectory = new File(jobExecutionContext.getString(JobExecutionKey.IMAGE_ROOT_DIR));
            final File actualStaticImagesDirectory =
                new File(jobExecutionContext.getString(JobExecutionKey.IMAGE_STATIC_DEST_DIR));
            final File actualDynamicImagesDirectory =
                new File(jobExecutionContext.getString(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));

            final File actualAssembleAssetsDirectory =
                new File(jobExecutionContext.getString(JobExecutionKey.ASSEMBLE_ASSETS_DIR));
            final File actualAssembleDocumentsDirectory =
                new File(jobExecutionContext.getString(JobExecutionKey.ASSEMBLE_DOCUMENTS_DIR));
            final File actualAssembleArtworkDirectory =
                new File(jobExecutionContext.getString(JobExecutionKey.ASSEMBLE_ARTWORK_DIR));

            Assert.assertEquals(expectedEbookDirectory.getAbsolutePath(), actualEbookDirectory.getAbsolutePath());
            Assert.assertEquals(expectedEbookFile.getAbsolutePath(), actualEbookFile.getAbsolutePath());

            Assert.assertTrue(actualImagesRootDirectory.exists());
            Assert.assertTrue(actualStaticImagesDirectory.exists());
            Assert.assertTrue(actualDynamicImagesDirectory.exists());
            Assert.assertTrue(actualAssembleAssetsDirectory.exists());
            Assert.assertTrue(actualAssembleDocumentsDirectory.exists());
            Assert.assertTrue(actualAssembleArtworkDirectory.exists());

            // Verify the transition to the next step
            Assert.assertEquals(ExitStatus.COMPLETED, transition);
            // Verify all collaborators were called as expected
            EasyMock.verify(chunkContext);
            EasyMock.verify(stepContext);
            EasyMock.verify(stepExecution);
            EasyMock.verify(jobExecution);
            EasyMock.verify(jobInstance);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        finally
        {
            final File dateDir = new File(tempRootDir, DATE_STAMP);
//			Assert.assertTrue("The date directory (yyyyMMdd) immediately below the root work directory exists", dateDir.exists());
            FileUtils.deleteDirectory(dateDir);
            Assert.assertFalse("The date directory has been recursively removed", dateDir.exists());
//			Assert.assertFalse(expectedWorkDirectory.exists());
        }
    }
}
