package com.thomsonreuters.uscl.ereader.request.step;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppMessageValidator;
import com.thomsonreuters.uscl.ereader.request.step.service.RetrieveBundleService;
import com.thomsonreuters.uscl.ereader.request.step.service.RetrieveBundleServiceImpl;
import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.*;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

public final class RetrieveBundleTaskTest {
    private RetrieveBundleTask tasklet;
    private XppMessageValidator mockValidator;
    private XppBundleArchiveDao mockArchiveDao;
    private EmailUtil emailUtil;
    private NotificationService mockNotificationService;
    private OutageProcessor mockOutageService;
    private RetrieveBundleService retrieveBundleService;

    private StepContribution mockContribution;
    private ChunkContext mockChunkContext;

    private JobParameters mockJobParameters;
    private ExecutionContext mockExecutionContext;
    private File ebookBundle;
    private File targetDir = new File("/apps/eBookBuilder/AutomatedTest/");

    private Capture<XppBundleArchive> capturedRequest;

    @Before
    public void setUp() throws IOException {
        mockValidator = EasyMock.createMock(XppMessageValidator.class);
        emailUtil = EasyMock.createMock(EmailUtil.class);
        mockNotificationService = EasyMock.createMock(NotificationService.class);
        mockOutageService = EasyMock.createMock(OutageProcessor.class);
        mockArchiveDao = EasyMock.createMock(XppBundleArchiveDao.class);

        tasklet = new RetrieveBundleTask(new RetrieveBundleServiceImpl(mockArchiveDao, mockValidator));
        tasklet.setEmailUtil(emailUtil);
        tasklet.setNotificationService(mockNotificationService);
        tasklet.setOutageProcessor(mockOutageService);

        mockContribution = EasyMock.createMock(StepContribution.class);
        mockChunkContext = EasyMock.createMock(ChunkContext.class);

        mockJobParameters = EasyMock.createMock(JobParameters.class);
        mockExecutionContext = EasyMock.createMock(ExecutionContext.class);
        ebookBundle = initBundleFile();

        capturedRequest = new Capture<>();
    }

    @After
    public void TearDown() throws IOException {
        FileUtils.deleteDirectory(ebookBundle.getParentFile());
        FileUtils.deleteDirectory(targetDir);
    }

    @Test
    @Ignore
    public void testHappyPath() throws Exception {
        ExitStatus exitCode = null;
        final XppBundleArchive expected = createRequest(
            "1.0",
            "ThisIsAnId",
            "ThisIsTheHash",
            new Date(1487201107046L),
            ebookBundle.getAbsolutePath());
        expected.setXppBundleArchiveId(1L);
        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.ENVIRONMENT_NAME)).andReturn("AutomatedTest");

        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        EasyMock.expect(mockExecutionContext.get(JobParameterKey.KEY_XPP_BUNDLE)).andReturn(expected);

        EasyMock.expect(mockArchiveDao.findFirstByMessageId("ThisIsAnId")).andReturn(null);

        mockValidator.validate(EasyMock.and(EasyMock.capture(capturedRequest), EasyMock.isA(XppBundleArchive.class)));

        EasyMock.expect(mockArchiveDao.save(expected)).andReturn(expected);
        replayAll();
        try {
            exitCode = tasklet.executeStep(mockContribution, mockChunkContext);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(ExitStatus.COMPLETED, exitCode);

        final XppBundleArchive request = capturedRequest.getValue();
        Assert.assertEquals(
            new File(targetDir.getAbsolutePath() + "/xpp/archive/2017/02/filename.gz"),
            request.getEBookSrcFile());
    }

    @Test
    public void testDuplicateRequest() {
        Exception e = null;
        final XppBundleArchive bundle = createRequest(
            "1.0",
            "ThisIsAnId",
            "ThisIsTheHash",
            new Date(1487201107046L),
            ebookBundle.getAbsolutePath());

        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.ENVIRONMENT_NAME)).andReturn("AutomatedTest");

        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        EasyMock.expect(mockExecutionContext.get(JobParameterKey.KEY_XPP_BUNDLE)).andReturn(bundle);

        EasyMock.expect(mockArchiveDao.findFirstByMessageId("ThisIsAnId")).andReturn(bundle);

        replayAll();
        try {
            tasklet.executeStep(mockContribution, mockChunkContext);
            Assert.fail("should throw XppMessageException");
        } catch (final Exception exc) {
            e = exc;
        }
        Assert.assertNotNull(e);
        Assert.assertEquals(XPPConstants.ERROR_DUPLICATE_REQUEST + bundle, e.getMessage());
    }

    private static File initBundleFile() throws IOException {
        final File ebookBundle = new File(System.getProperty("java.io.tmpdir") + "/EvenMoreTemp/filename.gz");
        ebookBundle.getParentFile().mkdirs();
        ebookBundle.createNewFile();
        return ebookBundle;
    }

    private static XppBundleArchive createRequest(
        final String version,
        final String messageId,
        final String bundleHash,
        final Date dateTime,
        final String ebookSrcFile) {
        final XppBundleArchive request = new XppBundleArchive();
        request.setVersion(version);
        request.setMessageId(messageId);
        request.setDateTime(dateTime);
        request.setEBookSrcFile(new File(ebookSrcFile));
        request.setBundleHash(bundleHash);
        return request;
    }

    private void mockGetJobParameters(final ChunkContext mockChunk, final JobParameters mockParameters) {
        final StepContext step = EasyMock.createMock(StepContext.class);
        final StepExecution stepExecution = EasyMock.createMock(StepExecution.class);

        EasyMock.expect(mockChunk.getStepContext()).andReturn(step);
        EasyMock.expect(step.getStepExecution()).andReturn(stepExecution);
        EasyMock.expect(stepExecution.getJobParameters()).andReturn(mockParameters);

        EasyMock.replay(step);
        EasyMock.replay(stepExecution);
    }

    private void mockGetJobExecutionContext(final ChunkContext mockChunk, final ExecutionContext mockExecution) {
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

    private void replayAll() {
        EasyMock.replay(mockValidator);
        EasyMock.replay(mockArchiveDao);
        EasyMock.replay(emailUtil);
        EasyMock.replay(mockNotificationService);
        EasyMock.replay(mockOutageService);
        EasyMock.replay(mockContribution);
        EasyMock.replay(mockChunkContext);
        EasyMock.replay(mockJobParameters);
        EasyMock.replay(mockExecutionContext);
    }
}
