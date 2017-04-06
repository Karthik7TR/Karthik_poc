package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.GZIPService;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleValidator;
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
    private GZIPService mockGZIP;
    private XppBundleValidator mockBundleValidator;
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
    public void setUp() throws Exception
    {
        mockGZIP = EasyMock.createMock(GZIPService.class);
        mockBundleValidator = EasyMock.createMock(XppBundleValidator.class);
        mockCoreService = EasyMock.createMock(CoreService.class);
        mockNotificationService = EasyMock.createMock(NotificationService.class);
        mockOutageService = EasyMock.createMock(OutageProcessor.class);

        tasklet = new StageBundleTask();
        tasklet.setGZIPService(mockGZIP);
        tasklet.setBundleValidator(mockBundleValidator);
        tasklet.setCoreService(mockCoreService);
        tasklet.setNotificationService(mockNotificationService);
        tasklet.setOutageProcessor(mockOutageService);

        mockContribution = EasyMock.createMock(StepContribution.class);
        mockChunkContext = EasyMock.createMock(ChunkContext.class);

        mockJobParameters = EasyMock.createMock(JobParameters.class);
        mockExecutionContext = EasyMock.createMock(ExecutionContext.class);
        tempRootDir = new File("/apps/eBookBuilder/AutomatedTest/");
        tempRootDir.mkdir();
        targetDir = new File(tempRootDir, "xpp/jobs/mockTarball");
        tarball = new File(tempRootDir.getAbsolutePath() + "/xpp/archive/mockTarball.gz");

        final String bundlexml =
            "<bundle><product_title>title</product_title><product_type>type</product_type></bundle>";
        writeFile(targetDir, "bundle.xml", bundlexml);
    }

    @After
    public void TearDown() throws IOException
    {
        FileUtils.deleteDirectory(tempRootDir);
    }

    @Test
    public void testHappyPath() throws Exception
    {
        ExitStatus exitCode = null;
        final XppBundleArchive request =
            createRequest("1.0", "ThisIsAnId", "ThisIsTheHash", new Date(1487201107046L), tarball.getAbsolutePath());
        final XppBundle bundle = createBundle("title", "type");
        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        EasyMock.expect(mockExecutionContext.get(JobParameterKey.KEY_XPP_BUNDLE)).andReturn(request);
        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.ENVIRONMENT_NAME)).andReturn("AutomatedTest");
        mockGZIP.untarzip(tarball, targetDir);
        EasyMock.expectLastCall();
        mockBundleValidator.validateBundleDirectory(targetDir);
        EasyMock.expectLastCall();
        mockBundleValidator.validateBundleXml(bundle);
        EasyMock.expectLastCall();
        replayAll();
        try
        {
            exitCode = tasklet.executeStep(mockContribution, mockChunkContext);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("exception thrown unexpectedly: " + e.getMessage());
        }
        Assert.assertEquals(ExitStatus.COMPLETED, exitCode);
    }

    private void writeFile(final File dir, final String name, final String content) throws Exception
    {
        dir.mkdirs();
        try (OutputStream outStream = new FileOutputStream(new File(dir, name)))
        {
            outStream.write(content.getBytes());
        }
    }

    private static XppBundleArchive createRequest(
        final String version,
        final String messageId,
        final String bundleHash,
        final Date dateTime,
        final String ebookSrcFile)
    {
        final XppBundleArchive request = new XppBundleArchive();
        request.setVersion(version);
        request.setMessageId(messageId);
        request.setDateTime(dateTime);
        request.setEBookSrcFile(new File(ebookSrcFile));
        request.setBundleHash(bundleHash);
        return request;
    }

    private static XppBundle createBundle(final String productTitle, final String productType)
    {
        final XppBundle bundle = new XppBundle();
        bundle.setProductTitle(productTitle);
        bundle.setProductType(productType);
        return bundle;
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
        EasyMock.replay(mockGZIP);
        EasyMock.replay(mockBundleValidator);
        EasyMock.replay(mockCoreService);
        EasyMock.replay(mockNotificationService);
        EasyMock.replay(mockOutageService);
        EasyMock.replay(mockContribution);
        EasyMock.replay(mockChunkContext);
        EasyMock.replay(mockJobParameters);
        EasyMock.replay(mockExecutionContext);
    }
}
