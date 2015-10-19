/**
 * 
 */
package com.thomsonreuters.uscl.ereader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * @author ravi.nandikolla@thomsonreuters.com c139353
 *
 */
public class SendingEmailNotification extends AbstractSbTasklet {
	
	private static final Logger log = Logger.getLogger(SendingEmailNotification.class);
	private PublishingStatsService publishingStatsService;
	private AutoSplitGuidsService autoSplitGuidsService;

	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		Long jobId = getJobInstance(chunkContext).getId();
		String publishStatus = "Completed";
		
		try {
			sendNotification(chunkContext);
		} catch (Exception e) {
			publishStatus = "Failed";
			log.error("Failed to send Email notification to the user ", e);
			throw e;
		} finally {
			PublishingStats jobstats = new PublishingStats();
		    jobstats.setJobInstanceId(jobId);
		    jobstats.setPublishStatus("sendEmailNotification : " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		
		return ExitStatus.COMPLETED;
	}
	
    private void sendNotification(ChunkContext chunkContext) throws Exception {
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
        if(!bookDefinition.isSplitBook()){
        	Integer tocNodeCount = publishingStatsService.findPublishingStatsByJobId(jobInstanceId).getGatherTocNodeCount();
        	Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();
        	String tocXmlFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE);
			if (tocNodeCount > thresholdValue){
				 subject = subject + "–Threshold Warning";
				 String msg =  getMetricsInfo(bookDefinition, tocNodeCount, jobInstanceId, tocXmlFile, thresholdValue);
				 body = body.concat(msg);
			}
        }
        
       

        EmailNotification.send(recipients, subject, body);
	}
    
	public String getMetricsInfo(BookDefinition bookDefinition, Integer tocNodeCount, long jobInstanceId,
			String tocXmlFile, Integer thresholdValue) {
		StringBuffer buffer = new StringBuffer();
		InputStream tocInputSteam = null;
		try {

			tocInputSteam = new FileInputStream(tocXmlFile);
			boolean metrics = true;
			Map<String, String> splitGuidTextMap = new HashMap<String, String>();
			List<String> splitTocGuidList = autoSplitGuidsService.getAutoSplitNodes(tocInputSteam, bookDefinition,
					tocNodeCount, jobInstanceId, metrics);
			splitGuidTextMap = autoSplitGuidsService.getSplitGuidTextMap();
			buffer.append("\n\n**WARNING**: The book exceeds threshold value "+thresholdValue);
			buffer.append("\nTotal node count is " + tocNodeCount);
			buffer.append("\n\nPlease find the below system suggested information");
			buffer.append("\n\nTotal split parts " + (splitTocGuidList.size() + 1));
			buffer.append("\n\nTOC/NORT guids \n\n");
			for (Map.Entry<String, String> entry : splitGuidTextMap.entrySet()) {
				String uuid = entry.getKey();
				String name = entry.getValue();
				buffer.append(uuid + "  :  " + name+"\n" );
			}
		} catch (IOException iox) {
			throw new RuntimeException("Unable to find File : " + tocXmlFile + " " + iox);
		} finally {
			if (tocInputSteam != null) {
				try {
					tocInputSteam.close();
				} catch (IOException e) {
					throw new RuntimeException("An IOException occurred while closing a file ", e);
				}
			}
		}

		return buffer.toString();

	}
	
	public String condenseToOneLine(String string) {
        string = string.replaceAll("\r", "");
        string = string.replaceAll("\n", "");
        string = string.replaceAll("\\s{2,}", "");
        return string;
    }
    
    public AutoSplitGuidsService getAutoSplitGuidsService() {
		return autoSplitGuidsService;
	}
	
	@Required
	public void setAutoSplitGuidsService(AutoSplitGuidsService autoSplitGuidsService) {
		this.autoSplitGuidsService = autoSplitGuidsService;
	}
	
    @Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
