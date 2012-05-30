/**
 * 
 */
package com.thomsonreuters.uscl.ereader;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * @author ravi.nandikolla@thomsonreuters.com c139353
 *
 */
public class SendingEmailNotification extends AbstractSbTasklet {
	
	private static final Logger Log = Logger.getLogger(SendingEmailNotification.class);

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		sendNotification(chunkContext);
		return ExitStatus.COMPLETED;
	}
	
    private void sendNotification(ChunkContext chunkContext) {
		Log.debug("Sending Email messgae to Email ID : " + JobParameterKey.USER_EMAIL);
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);

		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();

		long jobInstanceId = stepExecution.getJobExecution().getJobInstance().getId();
		long jobExecutionId = stepExecution.getJobExecutionId();

		String userEmailId =jobParams.getString(JobParameterKey.USER_EMAIL);
		String jobOwnerEmail = jobParams.getString(JobParameterKey.JOB_OWNERS_GROUP_EMAIL);
		String environment  = jobParams.getString(JobParameterKey.ENVIRONMENT_NAME);
        String emailAdd;
        

        if(userEmailId != null &&  !userEmailId.isEmpty())
        {
        	emailAdd= userEmailId;	
        }else{
        	emailAdd = jobOwnerEmail;
        }

		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(EBOOK_DEFINITON);
       
        
        String moreInfo =  " " + environment +"  "+bookDefinition.getTitleId() +"  "+bookDefinition.getProviewDisplayName()+ "  " +jobInstanceId +"  "+jobExecutionId;
        String subject = "Publishing Successfully : " + moreInfo ;
        System.out.println(" ************************************************* ");
        System.out.println(" subject :"+subject);
        System.out.println(" emailAdd :"+emailAdd);
        System.out.println(" ************************************************* ");
        EmailNotification.send(emailAdd, subject, " Publishing Successfully Completed! "+"  \n"+ moreInfo);
		
	}

}
