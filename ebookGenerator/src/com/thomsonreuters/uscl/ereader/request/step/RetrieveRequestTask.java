package com.thomsonreuters.uscl.ereader.request.step;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.jms.exception.MessageQueueException;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.request.service.EBookRequestValidator;
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
    private static final Logger log = LogManager.getLogger(RetrieveRequestTask.class);
    private EBookRequestValidator eBookRequestValidator;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
    {
        log.debug("Retrieving request...");
        final JobParameters jobParameters = getJobParameters(chunkContext);

        EBookRequest eBookRequest = null;
        final String request = jobParameters.getString(JobParameterKey.KEY_REQUEST_XML);

        try
        {
            //log.debug(request); // log raw request
            eBookRequest = unmarshalRequest(request);
            // validate message content
            eBookRequestValidator.validate(eBookRequest);
            // archive request to database
            //archiveRequest(eBookRequest);
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
        jobExecutionContext.put(JobParameterKey.KEY_EBOOK_REQUEST, eBookRequest);
        return ExitStatus.COMPLETED;
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

    @Required
    public void setEBookRequestValidator(final EBookRequestValidator eBookRequestValidator)
    {
        this.eBookRequestValidator = eBookRequestValidator;
    }

    public EBookRequestValidator getEBookRequestValidator()
    {
        return eBookRequestValidator;
    }
}
