package com.thomsonreuters.uscl.ereader.request.step;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.service.EmailUtil;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppMessageValidator;
import org.easymock.Capture;
import org.easymock.EasyMock;
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

public final class ProcessMessageTaskTest {
    private static final String MESSAGE_ID = "Ia9e003a05bdd11e7991a005056b56b77";
    private static final String BUNDLE_HASH = "1919244a0a6dda548aabbf426ae3a45f";
    private static final String DATE_TIME_CURRENT_TIMEZONE = "2016-01-21T23:42:03.522Z";
    private static final String DATE_TIME_COLON_DIVIDED_TIMEZONE = "2017-06-23T14:01:22.644+03:00";
    private static final String DATE_TIME_NO_COLON_TIMEZONE = "2017-06-23T14:01:22.644+0300";
    private static final String DATE_TIME_GMT_TIMEZONE = "2017-06-28T15:34:33.900GMT";
    private static final String SRC_FILE =
        "/apps/eBookBuilder/cicontent/xpp/drop/FLLB_22992299_2017-06-28_00.00.00.00.-0000.tar.gz";
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String REQUEST =
        "<eBookRequest version=\"1.0\"><messageId>%s</messageId><bundleHash>%s</bundleHash><dateTime>%s</dateTime><srcFile>%s</srcFile><materialNumber>%s</materialNumber></eBookRequest>";

    private ProcessMessageTask tasklet;
    private XppMessageValidator mockValidator;
    private EmailUtil emailUtil;
    private NotificationService mockNotificationService;
    private OutageProcessor mockOutageService;

    private StepContribution mockContribution;
    private ChunkContext mockChunkContext;

    private JobParameters mockJobParameters;
    private ExecutionContext mockExecutionContext;

    private Capture<XppBundleArchive> capturedRequest;

    @Before
    public void setUp() {
        mockValidator = EasyMock.createMock(XppMessageValidator.class);
        emailUtil = EasyMock.createMock(EmailUtil.class);
        mockNotificationService = EasyMock.createMock(NotificationService.class);
        mockOutageService = EasyMock.createMock(OutageProcessor.class);

        tasklet = new ProcessMessageTask();
        tasklet.setXppMessageValidator(mockValidator);
        tasklet.setEmailUtil(emailUtil);
        tasklet.setNotificationService(mockNotificationService);
        tasklet.setOutageProcessor(mockOutageService);

        mockContribution = EasyMock.createMock(StepContribution.class);
        mockChunkContext = EasyMock.createMock(ChunkContext.class);

        mockJobParameters = EasyMock.createMock(JobParameters.class);
        mockExecutionContext = EasyMock.createMock(ExecutionContext.class);

        capturedRequest = new Capture<>();
    }

    @Test
    public void testHappyPath() throws Exception {
        final XppBundleArchive expected =
            createRequest("1.0", "ThisIsAnId", "ThisIsTheHash", new Date(), "ThisIsTheFileLocation", 127L);
        final String requestXML = createXML(expected);
        expected.setMessageRequest(requestXML);
        ExitStatus exitCode = null;

        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.KEY_REQUEST_XML)).andReturn(requestXML);
        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        mockValidator.validate(EasyMock.anyObject(XppBundleArchive.class));
        EasyMock.expectLastCall().once();
        mockExecutionContext.put(
            EasyMock.matches(JobParameterKey.KEY_XPP_BUNDLE),
            EasyMock.and(EasyMock.capture(capturedRequest), EasyMock.isA(XppBundleArchive.class)));
        replayAll();
        try {
            exitCode = tasklet.executeStep(mockContribution, mockChunkContext);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(ExitStatus.COMPLETED, exitCode);
        final XppBundleArchive request = capturedRequest.getValue();
        Assert.assertEquals(expected, request);
    }

    @Test
    public void testValidationException() throws Exception {
        final XppBundleArchive expected =
            createRequest("1.0", "ThisIsAnId", "ThisIsTheHash", new Date(), "ThisIsTheFileLocation", 127L);
        final String requestXML = createXML(expected);
        ExitStatus exitCode = null;

        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.KEY_REQUEST_XML)).andReturn(requestXML);
        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        mockValidator.validate(EasyMock.anyObject(XppBundleArchive.class));
        EasyMock.expectLastCall().andThrow(new XppMessageException(""));
        replayAll();
        try {
            exitCode = tasklet.executeStep(mockContribution, mockChunkContext);
        } catch (final Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(ExitStatus.FAILED, exitCode);
    }

    @Test
    public void shouldUnmarshallRequest() throws Exception {
        //when
        final XppBundleArchive xppBundleArchive = tasklet.unmarshalRequest(
            String.format(REQUEST, MESSAGE_ID, BUNDLE_HASH, DATE_TIME_CURRENT_TIMEZONE, SRC_FILE, MATERIAL_NUMBER));
        //then
        Assert.assertEquals(MESSAGE_ID, xppBundleArchive.getMessageId());
        Assert.assertEquals(BUNDLE_HASH, xppBundleArchive.getBundleHash());
        Assert.assertNotNull(xppBundleArchive.getDateTime());
        Assert.assertEquals(SRC_FILE, xppBundleArchive.getEBookSrcPath());
        Assert.assertEquals(MATERIAL_NUMBER, xppBundleArchive.getMaterialNumber());
    }

    @Test
    public void shouldUnmarshallTimezoneDividedByColon() throws Exception {
        //when
        final XppBundleArchive xppBundleArchive = tasklet.unmarshalRequest(
            String
                .format(REQUEST, MESSAGE_ID, BUNDLE_HASH, DATE_TIME_COLON_DIVIDED_TIMEZONE, SRC_FILE, MATERIAL_NUMBER));
        //then
        Assert.assertNotNull(xppBundleArchive.getDateTime());
    }

    @Test
    public void shouldNotUnmarshallNoColonTimezone() throws Exception {
        //when
        final XppBundleArchive xppBundleArchive = tasklet.unmarshalRequest(
            String.format(REQUEST, MESSAGE_ID, BUNDLE_HASH, DATE_TIME_NO_COLON_TIMEZONE, SRC_FILE, MATERIAL_NUMBER));
        //then
        Assert.assertNull(xppBundleArchive.getDateTime());
    }

    @Test
    public void shouldNotUnmarshallGmtTimezone() throws Exception {
        //when
        final XppBundleArchive xppBundleArchive = tasklet.unmarshalRequest(
            String.format(REQUEST, MESSAGE_ID, BUNDLE_HASH, DATE_TIME_GMT_TIMEZONE, SRC_FILE, MATERIAL_NUMBER));
        //then
        Assert.assertNull(xppBundleArchive.getDateTime());
    }

    private static XppBundleArchive createRequest(
        final String version,
        final String messageId,
        final String bundleHash,
        final Date dateTime,
        final String ebookSrcFile,
        final long materialNumber) {
        final XppBundleArchive request = new XppBundleArchive();
        request.setVersion(version);
        request.setMessageId(messageId);
        request.setDateTime(dateTime);
        request.setEBookSrcFile(new File(ebookSrcFile));
        request.setBundleHash(bundleHash);
        request.setMaterialNumber(String.valueOf(materialNumber));
        return request;
    }

    private static String createXML(final XppBundleArchive request) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(XppBundleArchive.class);
        final Marshaller marshaller = context.createMarshaller();

        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        marshaller.marshal(request, outStream);
        return outStream.toString();
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
        EasyMock.replay(emailUtil);
        EasyMock.replay(mockNotificationService);
        EasyMock.replay(mockOutageService);
        EasyMock.replay(mockContribution);
        EasyMock.replay(mockChunkContext);
        EasyMock.replay(mockJobParameters);
        EasyMock.replay(mockExecutionContext);
    }
}
