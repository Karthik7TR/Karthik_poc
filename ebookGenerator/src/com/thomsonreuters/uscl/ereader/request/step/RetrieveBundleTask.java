package com.thomsonreuters.uscl.ereader.request.step;

import java.util.HashSet;

import javax.mail.internet.InternetAddress;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

public class RetrieveBundleTask extends AbstractSbTasklet
{
    private static final Logger log = LogManager.getLogger(RetrieveBundleTask.class);

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        // TODO Auto-generated method stub
        log.debug("Extracting Bundle...");
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
}
