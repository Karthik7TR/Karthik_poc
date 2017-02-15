package com.thomsonreuters.uscl.ereader.request.step;

import java.io.ByteArrayInputStream;
import java.util.HashSet;

import javax.mail.internet.InternetAddress;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.jms.exception.MessageQueueException;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.dao.BundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.service.EBookRequestValidator;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

public class RetrieveRequestTask extends AbstractSbTasklet
{
    public static final String ERROR_DUPLICATE_REQUEST = "Request already received";

    private static final Logger log = LogManager.getLogger(RetrieveRequestTask.class);
    private EBookRequestValidator eBookRequestValidator;
    private BundleArchiveDao bundleArchiveDao;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
    {
        log.debug("Retrieving request...");
        EBookRequest eBookRequest = null;
        final String request =
            (String) chunkContext.getStepContext().getJobParameters().get(EBookRequest.KEY_REQUEST_XML);

        try
        {
            //log.debug(request); // log raw request
            eBookRequest = unmarshalRequest(request);
            // validate message content
            eBookRequestValidator.validate(eBookRequest);
            archiveRequest(eBookRequest);
            if (eBookRequest == null)
            {
                throw new MessageQueueException("Message not resolved");
            }
        }
        catch (final JAXBException e)
        {
            // TODO: handle unmarshalling exception
            log.error("request cannot be parsed: " + request);
            return ExitStatus.FAILED;
        }
        catch (final MessageQueueException e)
        {
            log.error("request is invalid: " + e.getMessage());
            // cannot process
            return ExitStatus.FAILED;
        }
        catch (final Exception e)
        {
            log.error("Exception encountered: " + e.getMessage());
            throw e;
        }

        log.debug("Request validated " + eBookRequest);
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        jobExecutionContext.put(EBookRequest.KEY_EBOOK_REQUEST, eBookRequest);
        return ExitStatus.COMPLETED;
    }

    @Override
    protected void sendNotification(
        final ChunkContext chunkContext,
        String bodyMessage,
        final long jobInstanceId,
        final long jobExecutionId)
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobParameters jobParams = getJobParameters(chunkContext);
        final String subject;
        final String failedJobInfo;
        final EBookRequest eBookRequest = (EBookRequest) jobExecutionContext.get(EBookRequest.KEY_EBOOK_REQUEST);
        final String jobEnvironment = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);

        if (eBookRequest != null)
        {
            failedJobInfo = "eBook Request Failure:  "
                + jobEnvironment
                + "  "
                + eBookRequest.getMessageId()
                + "  "
                + eBookRequest.getProductName()
                + "  "
                + jobInstanceId
                + "  "
                + jobExecutionId;
        }
        else
        {
            failedJobInfo = "eBook Request Failure:  " + jobParams.getParameters().get(EBookRequest.KEY_REQUEST_XML);
        }
        bodyMessage = failedJobInfo + "  \n" + bodyMessage;
        subject = failedJobInfo;

        EmailNotification.send(new HashSet<InternetAddress>(), subject, bodyMessage);
    }

    private EBookRequest unmarshalRequest(final String request) throws JAXBException
    {
        final JAXBContext context = JAXBContext.newInstance(EBookRequest.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        final EBookRequest eBookRequest =
            (EBookRequest) unmarshaller.unmarshal(new ByteArrayInputStream(request.getBytes()));
        eBookRequest.setMessageRequest(request);
        return eBookRequest;
    }

    private void archiveRequest(final EBookRequest request) throws MessageQueueException
    {
        final EBookRequest dup = bundleArchiveDao.findByRequestId(request.getMessageId());
        if (dup != null)
        {
            if (dup.equals(request))
            {
                throw new MessageQueueException(ERROR_DUPLICATE_REQUEST);
            }
            else
            {
                // two non-identical requests have been received with the same ID
                // TODO identify steps for processing this scenario
                throw new MessageQueueException("non-identical duplicate request received");
            }
        }
        else
        {
            bundleArchiveDao.saveRequest(request);
        }
    }

    @Required
    public void setEBookRequestValidator(final EBookRequestValidator eBookRequestValidator)
    {
        this.eBookRequestValidator = eBookRequestValidator;
    }

    public EBookRequestValidator getEBookRequestValidator()
    {
        return eBookRequestValidator;
    }

    @Required
    public void setBundleArchiveDao(final BundleArchiveDao bundleArchiveDao)
    {
        this.bundleArchiveDao = bundleArchiveDao;
    }

    public BundleArchiveDao getBundleArchiveDao()
    {
        return bundleArchiveDao;
    }
}
