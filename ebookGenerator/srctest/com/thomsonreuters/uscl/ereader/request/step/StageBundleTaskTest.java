package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.EBookRequestException;
import com.thomsonreuters.uscl.ereader.request.dao.BundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.service.GZIPService;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

public final class StageBundleTaskTest
{
    private StageBundleTask tasklet;
    private BundleArchiveDao mockDao;
    private GZIPService mockGZIP;
    private CoreService mockCoreService;
    private NotificationService mockNotificationService;
    private OutageProcessor mockOutageService;

    private StepContribution mockContribution;
    private ChunkContext mockChunkContext;

    private JobParameters mockJobParameters;
    private ExecutionContext mockExecutionContext;
    private File tempRootDir;
    private File targetDir;
    private File tarball;

    @Before
    public void setUp()
    {
        mockDao = EasyMock.createMock(BundleArchiveDao.class);
        mockGZIP = EasyMock.createMock(GZIPService.class);
        mockCoreService = EasyMock.createMock(CoreService.class);
        mockNotificationService = EasyMock.createMock(NotificationService.class);
        mockOutageService = EasyMock.createMock(OutageProcessor.class);

        tasklet = new StageBundleTask();
        tasklet.setBundleArchiveDao(mockDao);
        tasklet.setGZIPService(mockGZIP);
        tasklet.setCoreService(mockCoreService);
        tasklet.setNotificationService(mockNotificationService);
        tasklet.setOutageProcessor(mockOutageService);

        mockContribution = EasyMock.createMock(StepContribution.class);
        mockChunkContext = EasyMock.createMock(ChunkContext.class);

        mockJobParameters = EasyMock.createMock(JobParameters.class);
        mockExecutionContext = EasyMock.createMock(ExecutionContext.class);
        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "/" + this.getClass().getName());
        tempRootDir.mkdir();
        targetDir = new File("/apps/eBookBuilder/AutomatedTest/xpp/jobs/mockTarball");
        tarball = new File(tempRootDir, "mockTarball.tar.gz");
    }

    @After
    public void TearDown() throws IOException
    {
        FileUtils.deleteDirectory(tempRootDir);
    }

    @Test
    public void testHappyPath() throws EBookRequestException
    {
        ExitStatus exitCode = null;
        final EBookRequest request =
            createRequest("1.0", "ThisIsAnId", "ThisIsTheHash", new Date(1487201107046L), tarball.getAbsolutePath());
        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        EasyMock.expect(mockExecutionContext.get(JobParameterKey.KEY_EBOOK_REQUEST)).andReturn(request);
        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.ENVIRONMENT_NAME)).andReturn("AutomatedTest");
        mockGZIP.untarzip(tarball, targetDir);
        EasyMock.expectLastCall();
        EasyMock.expect(mockDao.findByRequestId(request.getMessageId())).andReturn(null);
        EasyMock.expect(mockDao.saveRequest(request)).andReturn(1L);
        replayAll();
        try
        {
            exitCode = tasklet.executeStep(mockContribution, mockChunkContext);
        }
        catch (final Exception e)
        {
            Assert.fail("exception thrown unexpectedly: " + e.getMessage());
        }
        Assert.assertEquals(ExitStatus.COMPLETED, exitCode);
    }

    @Test
    public void testDuplicateRequest() throws EBookRequestException
    {
        String errorMessage = null;
        final EBookRequest request =
            createRequest("1.0", "ThisIsAnId", "ThisIsTheHash", new Date(1487201107046L), tarball.getAbsolutePath());
        final String expectedError = String.format(StageBundleTask.ERROR_DUPLICATE_REQUEST, request.getMessageId());

        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        EasyMock.expect(mockExecutionContext.get(JobParameterKey.KEY_EBOOK_REQUEST)).andReturn(request);
        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.ENVIRONMENT_NAME)).andReturn("AutomatedTest");
        mockGZIP.untarzip(tarball, targetDir);
        EasyMock.expectLastCall();
        EasyMock.expect(mockDao.findByRequestId(request.getMessageId())).andReturn(request);
        replayAll();
        try
        {
            tasklet.executeStep(mockContribution, mockChunkContext);
        }
        catch (final Exception e)
        {
            errorMessage = e.getMessage();
        }
        Assert.assertEquals(expectedError, errorMessage);
    }

    private static EBookRequest createRequest(
        final String version,
        final String messageId,
        final String bundleHash,
        final Date dateTime,
        final String ebookSrcFile)
    {
        final EBookRequest request = new EBookRequest();
        request.setVersion(version);
        request.setMessageId(messageId);
        request.setDateTime(dateTime);
        request.setEBookSrcFile(new File(ebookSrcFile));
        request.setBundleHash(bundleHash);
        return request;
    }

    private void mockGetJobExecutionContext(final ChunkContext mockChunk, final ExecutionContext mockExecution)
    {
        final StepContext step = EasyMock.createMock(StepContext.class);
        final StepExecution stepExecution = EasyMock.createMock(StepExecution.class);
        final JobExecution jobExecution = EasyMock.createMock(JobExecution.class);

        EasyMock.expect(mockChunk.getStepContext()).andReturn(step);
        EasyMock.expect(step.getStepExecution()).andReturn(stepExecution);
        EasyMock.expect(stepExecution.getJobExecution()).andReturn(jobExecution);
        EasyMock.expect(jobExecution.getExecutionContext()).andReturn(mockExecution);

        EasyMock.replay(step);
        EasyMock.replay(stepExecution);
        EasyMock.replay(jobExecution);
    }

    private void mockGetJobParameters(final ChunkContext mockChunk, final JobParameters mockParameters)
    {
        final StepContext step = EasyMock.createMock(StepContext.class);
        final StepExecution stepExecution = EasyMock.createMock(StepExecution.class);

        EasyMock.expect(mockChunk.getStepContext()).andReturn(step);
        EasyMock.expect(step.getStepExecution()).andReturn(stepExecution);
        EasyMock.expect(stepExecution.getJobParameters()).andReturn(mockParameters);

        EasyMock.replay(step);
        EasyMock.replay(stepExecution);
    }

    private void replayAll()
    {
        EasyMock.replay(mockDao);
        EasyMock.replay(mockGZIP);
        EasyMock.replay(mockCoreService);
        EasyMock.replay(mockNotificationService);
        EasyMock.replay(mockOutageService);
        EasyMock.replay(mockContribution);
        EasyMock.replay(mockChunkContext);
        EasyMock.replay(mockJobParameters);
        EasyMock.replay(mockExecutionContext);
    }
}
