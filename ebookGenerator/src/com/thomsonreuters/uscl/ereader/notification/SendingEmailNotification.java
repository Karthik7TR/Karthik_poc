/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.notification;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

/**
 * @author ravi.nandikolla@thomsonreuters.com c139353
 *
 */
public class SendingEmailNotification extends AbstractSbTasklet {
	private static final Logger log = LogManager.getLogger(SendingEmailNotification.class);
	private PublishingStatsService publishingStatsService;
	private AutoSplitGuidsService autoSplitGuidsService;
	private DocMetadataService docMetadataService;

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		String publishStatus = "Completed";
		try {
			NotificationInfo info = getNotificationInfo(chunkContext);

			Collection<InternetAddress> recipients = coreService.getEmailRecipientsByUsername(info.getUserName());
			log.debug("Sending job completion notification to: " + recipients);
			String subject = getSubject(info);
			String body = getBody(info);
			EmailNotification.send(recipients, subject, body);
		} catch (Exception e) {
			publishStatus = "Failed";
			log.error("Failed to send Email notification to the user ", e);
			throw e;
		} finally {
			PublishingStats jobstats = new PublishingStats();
			jobstats.setJobInstanceId(getJobInstance(chunkContext).getId());
			jobstats.setPublishStatus("sendEmailNotification : " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		return ExitStatus.COMPLETED;
	}

	NotificationInfo getNotificationInfo(ChunkContext chunkContext) {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobParameters jobParams = getJobParameters(chunkContext);
		
		StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
		long jobInstanceId = stepExecution.getJobExecution().getJobInstance().getId();
		long jobExecutionId = stepExecution.getJobExecutionId();
		PublishingStats publishingStats = publishingStatsService.findPublishingStatsByJobId(jobInstanceId);
		if (publishingStats == null) {
			throw new RuntimeException("publishingStats not found for jobInstanceId=" + jobInstanceId);
		}
		PublishingStats previousStats = publishingStatsService.getPreviousPublishingStatsForSameBook(jobInstanceId);
		return new NotificationInfo(jobExecutionContext, jobParams, jobInstanceId, jobExecutionId, publishingStats, previousStats);
	}

	String getSubject(NotificationInfo info) {
		BookDefinition bookDefinition = info.getBookDefinition();
		StringBuilder sb = new StringBuilder();
		sb.append("eBook Publishing Successful - " + bookDefinition.getFullyQualifiedTitleId());
		if (bookDefinition.isSplitBook()) {
			sb.append(" (Split Book)");
		} else if (info.isBigToc()) {
			sb.append(" THRESHOLD WARNING");
		}
		return sb.toString();
	}

	String getBody(NotificationInfo info) {
		BookDefinition bookDefinition = info.getBookDefinition();
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String proviewDisplayName = bookDefinition.getProviewDisplayName();

		StringBuilder sb = new StringBuilder();
		sb.append("eBook Publishing Successful - " + fullyQualifiedTitleId);
		sb.append("\t\nProview Display Name: " + proviewDisplayName);
		sb.append("\t\nTitle ID: " + fullyQualifiedTitleId);
		sb.append("\t\nEnvironment: " + info.getEnvironment());
		sb.append("\t\nJob Instance ID: " + info.getJobInstanceId());
		sb.append("\t\nJob Execution ID: " + info.getJobExecutionId());
		
		sb.append(getVersionInfo(true, info));
		sb.append(getVersionInfo(false, info));

		if (bookDefinition.isSplitBook()) {
			sb.append(getBookPartsAndTitle(info));
		} else if (info.isBigToc()) {
			sb.append(getMetricsInfo(info));
		}
		return sb.toString();
	}

	String getVersionInfo(boolean isCurrent, NotificationInfo info) {
		PublishingStats stats = isCurrent ? info.getCurrentPublishingStats() : info.getPreviousPublishingStats();
		if (stats == null) {
			return "\t\n\t\nNo Previous Version.";
		}
		Long bookSize = stats.getBookSize();
		Integer docRetrievedCount = stats.getGatherDocRetrievedCount();
		StringBuilder sb = new StringBuilder();
		sb.append("\t\n\t\n");
		sb.append(isCurrent ? "Current Version:" : "Previous Version:");
		sb.append("\t\nJob Instance ID: " + stats.getJobInstanceId());
		sb.append("\t\nGather Doc Retrieved Count: " + (docRetrievedCount == null ? "-" : docRetrievedCount));
		sb.append("\t\nBook Size: " + (bookSize == null ? "-" : bookSize));
		return sb.toString();
	}

	String getBookPartsAndTitle(NotificationInfo info) {
		BookDefinition bookDefinition = info.getBookDefinition();
		String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
		String proviewDisplayName = bookDefinition.getProviewDisplayName();
		List<String> splitTitles = docMetadataService.findDistinctSplitTitlesByJobId(info.getJobInstanceId());

		StringBuilder sb = new StringBuilder();
		sb.append("\t\n\t\nPlease find the below information regarding the split titles");
		sb.append("\t\nProview display name : " + proviewDisplayName);
		sb.append("\t\nFully Qualified Title : " + fullyQualifiedTitleId);
		sb.append("\t\nTotal parts : " + splitTitles.size());
		sb.append("\t\nSplit Title Id's :");
		for (String splitTitleId : splitTitles) {
			sb.append("\t\n" + splitTitleId);
		}
		return sb.toString();
	}

	String getMetricsInfo(NotificationInfo info) {
		int totalSplitParts = getTotalSplitParts(info);
		Map<String, String> splitGuidTextMap = autoSplitGuidsService.getSplitGuidTextMap();

		StringBuilder sb = new StringBuilder();
		sb.append("\t\n\t\n**WARNING**: The book exceeds threshold value " + info.getThresholdValue());
		sb.append("\t\nTotal node count is " + info.getTocNodeCount());
		sb.append("\t\nPlease find the below system suggested information");
		sb.append("\t\nTotal split parts : " + totalSplitParts);
		sb.append("\t\nTOC/NORT guids :");
		for (Map.Entry<String, String> entry : splitGuidTextMap.entrySet()) {
			String uuid = entry.getKey();
			String name = entry.getValue();
			sb.append("\t\n" + uuid + "  :  " + name);
		}
		return sb.toString();
	}

	int getTotalSplitParts(NotificationInfo info) {
		BookDefinition bookDefinition = info.getBookDefinition();
		String tocXmlFile = getRequiredStringProperty(info.getJobExecutionContext(), JobExecutionKey.GATHER_TOC_FILE);
		Integer tocNodeCount = info.getTocNodeCount();
		Long jobInstanceId = info.getJobInstanceId();

		try (InputStream tocInputSteam = new FileInputStream(tocXmlFile)) {
			return autoSplitGuidsService
					.getAutoSplitNodes(tocInputSteam, bookDefinition, tocNodeCount, jobInstanceId, true).size() + 1;
		} catch (IOException e) {
			throw new RuntimeException("Cannot read file " + tocXmlFile, e);
		}
	}

	@Required
	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
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
