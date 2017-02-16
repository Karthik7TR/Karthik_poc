package com.thomsonreuters.uscl.ereader.request.step;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessor;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.jms.exception.MessageQueueException;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.NotificationService;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.service.EBookRequestValidator;
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

public final class RetrieveRequestTaskTest
{
    private RetrieveRequestTask tasklet;
    private EBookRequestValidator mockValidator;
    private CoreService mockCoreService;
    private NotificationService mockNotificationService;
    private OutageProcessor mockOutageService;

    private StepContribution mockContribution;
    private ChunkContext mockChunkContext;

    private JobParameters mockJobParameters;
    private ExecutionContext mockExecutionContext;

    private Capture<EBookRequest> capturedRequest;

    @Before
    public void setUp()
    {
        mockValidator = EasyMock.createMock(EBookRequestValidator.class);
        mockCoreService = EasyMock.createMock(CoreService.class);
        mockNotificationService = EasyMock.createMock(NotificationService.class);
        mockOutageService = EasyMock.createMock(OutageProcessor.class);

        tasklet = new RetrieveRequestTask();
        tasklet.setEBookRequestValidator(mockValidator);
        tasklet.setCoreService(mockCoreService);
        tasklet.setNotificationService(mockNotificationService);
        tasklet.setOutageProcessor(mockOutageService);

        mockContribution = EasyMock.createMock(StepContribution.class);
        mockChunkContext = EasyMock.createMock(ChunkContext.class);

        mockJobParameters = EasyMock.createMock(JobParameters.class);
        mockExecutionContext = EasyMock.createMock(ExecutionContext.class);

        capturedRequest = new Capture<>();
    }

    @Test
    public void testHappyPath() throws Exception
    {
        final EBookRequest expected =
            createRequest("1.0", "ThisIsAnId", "ThisIsTheHash", new Date(), "ThisIsTheFileLocation");
        final String requestXML = createXML(expected);
        ExitStatus exitCode = null;

        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.KEY_REQUEST_XML)).andReturn(requestXML);
        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        mockValidator.validate(EasyMock.anyObject(EBookRequest.class));
        EasyMock.expectLastCall().once();
        mockExecutionContext.put(
            EasyMock.matches(JobParameterKey.KEY_EBOOK_REQUEST),
            EasyMock.and(EasyMock.capture(capturedRequest), EasyMock.isA(EBookRequest.class)));
        replayAll();
        try
        {
            exitCode = tasklet.executeStep(mockContribution, mockChunkContext);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(ExitStatus.COMPLETED, exitCode);
        final EBookRequest request = capturedRequest.getValue();
        Assert.assertEquals(expected, request);
    }

    @Test
    public void testValidationException() throws Exception
    {
        final EBookRequest expected =
            createRequest("1.0", "ThisIsAnId", "ThisIsTheHash", new Date(), "ThisIsTheFileLocation");
        final String requestXML = createXML(expected);
        ExitStatus exitCode = null;

        mockGetJobParameters(mockChunkContext, mockJobParameters);
        EasyMock.expect(mockJobParameters.getString(JobParameterKey.KEY_REQUEST_XML)).andReturn(requestXML);
        mockGetJobExecutionContext(mockChunkContext, mockExecutionContext);
        mockValidator.validate(EasyMock.anyObject(EBookRequest.class));
        EasyMock.expectLastCall().andThrow(new MessageQueueException(""));
        replayAll();
        try
        {
            exitCode = tasklet.executeStep(mockContribution, mockChunkContext);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(ExitStatus.FAILED, exitCode);
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

    private static String createXML(final EBookRequest request) throws JAXBException
    {
        final JAXBContext context = JAXBContext.newInstance(EBookRequest.class);
        final Marshaller marshaller = context.createMarshaller();

        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        marshaller.marshal(request, outStream);
        return outStream.toString();
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

    private void replayAll()
    {
        EasyMock.replay(mockValidator);
        EasyMock.replay(mockCoreService);
        EasyMock.replay(mockNotificationService);
        EasyMock.replay(mockOutageService);
        EasyMock.replay(mockContribution);
        EasyMock.replay(mockChunkContext);
        EasyMock.replay(mockJobParameters);
        EasyMock.replay(mockExecutionContext);
    }
}
