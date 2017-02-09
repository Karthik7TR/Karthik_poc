package com.thomsonreuters.uscl.ereader.request.step;

import java.util.HashSet;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.request.EBookRequest;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

public class StageBundleTask extends AbstractSbTasklet {
	private static final Logger log = LogManager.getLogger(StageBundleTask.class);
	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		// TODO Auto-generated method stub
		log.debug("Staging Bundle...");
		return ExitStatus.COMPLETED;
	}
	

	
	@Override
	protected void sendNotification(ChunkContext chunkContext, String bodyMessage, long jobInstanceId, long jobExecutionId) {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);

		String subject;
		String failedJobInfo;
		EBookRequest eBookRequest = (EBookRequest) jobExecutionContext.get(EBookRequest.KEY_EBOOK_REQUEST);
		String jobEnvironment = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);

		if (eBookRequest != null) {
			failedJobInfo = "eBook Request Failure:  " + jobEnvironment + "  " + eBookRequest.getMessageId() + "  " + eBookRequest
					.getProductName() + "  " + jobInstanceId + "  " + jobExecutionId;
		} else {
			failedJobInfo = "eBook Request Failure:  " + jobParams.getParameters().get(EBookRequest.KEY_REQUEST_XML);
		}
		bodyMessage = failedJobInfo + "  \n" + bodyMessage;
		subject = failedJobInfo;

		EmailNotification.send(new HashSet<InternetAddress>(), subject, bodyMessage);
	}
}