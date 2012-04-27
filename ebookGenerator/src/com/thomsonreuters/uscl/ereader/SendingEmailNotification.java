/**
 * 
 */
package com.thomsonreuters.uscl.ereader;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
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
		String emailId = JobParameterKey.USER_EMAIL;
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(EBOOK_DEFINITON);
        String subject = bookDefinition.getTitleId() + "  " + bookDefinition.getProviewDisplayName();
        EmailNotification.send(emailId, subject, " Publishing Succefully Completed!");
		
	}

}
