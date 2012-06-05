/**
 * 
 */
package com.thomsonreuters.uscl.ereader;

import java.util.Collection;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.service.CoreService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * @author ravi.nandikolla@thomsonreuters.com c139353
 *
 */
public class SendingEmailNotification extends AbstractSbTasklet {
	
	private static final Logger log = Logger.getLogger(SendingEmailNotification.class);
	
	private CoreService coreService;

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		sendNotification(chunkContext);
		return ExitStatus.COMPLETED;
	}
	
    private void sendNotification(ChunkContext chunkContext) {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);

		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();

		long jobInstanceId = stepExecution.getJobExecution().getJobInstance().getId();
		long jobExecutionId = stepExecution.getJobExecutionId();

		String username = jobParams.getString(JobParameterKey.USER_NAME);
		Collection<InternetAddress> recipients = coreService.getEmailRecipientsByUsername(username);
		log.debug("Sending job completion notification to: " + recipients);
		
		String environment  = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(EBOOK_DEFINITON);
        
		String subject = "eBook Publishing Successful - " + bookDefinition.getFullyQualifiedTitleId();
        String body =  String.format("%s\n\nProview Display Name: %s \nTitle ID: %s \nJob Instance ID: %d \nJob Execution ID: %d \nEnvironment: %s\n",
        					subject, bookDefinition.getProviewDisplayName(), bookDefinition.getFullyQualifiedTitleId(),
        					jobInstanceId, jobExecutionId, environment);
        EmailNotification.send(recipients, subject, body);
	}
    
    @Required
    public void setCoreService(CoreService service) {
    	this.coreService = service;
    }

}
