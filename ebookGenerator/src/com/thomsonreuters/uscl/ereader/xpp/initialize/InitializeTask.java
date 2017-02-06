/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.xpp.initialize;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.IllegalStateException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for use in later steps. This
 * includes various file system path calculations based on the JobParameters used to run the job.
 */
public class InitializeTask extends AbstractSbTasklet {
  private static final Logger LOG = LogManager.getLogger(InitializeTask.class);

  private File rootWorkDirectory; // "/nas/ebookbuilder/data"
  private String environmentName;
  private PublishingStatsService publishingStatsService;
  private EBookAuditService eBookAuditService;
  private BookDefinitionService bookDefnService;

  @Override
  public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
    JobInstance jobInstance = jobExecution.getJobInstance();
    JobParameters jobParams = jobExecution.getJobParameters();
    PublishingStatus publishStatus = PublishingStatus.COMPLETED;
    try {
      BookDefinition bookDefinition = getBookDefinition(jobExecutionContext, jobParams);
      createWorkDir(jobExecutionContext, jobInstance, bookDefinition);

      LOG.debug("Proview Domain URL: " + System.getProperty("proview.domain"));
    } catch (Exception e) {
      publishStatus = PublishingStatus.FAILED;
      throw e;
    } finally {
      createPublishingStats(jobInstance, jobParams, publishStatus);
    }
    return ExitStatus.COMPLETED;
  }

  private BookDefinition getBookDefinition(ExecutionContext jobExecutionContext, JobParameters jobParams) {
    BookDefinition bookDefinition = bookDefnService
        .findBookDefinitionByEbookDefId(jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID));
    jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
    LOG.debug("titleId (Fully Qualified): " + bookDefinition.getTitleId());
    LOG.debug("hostname: " + jobParams.getString(JobParameterKey.HOST_NAME));
    return bookDefinition;
  }

  /**
   * Create the work directory for the ebook and create the physical directory in the filesystem
   * "yyyyMMdd/titleId/jobInstanceId". Sample: "/apps/eBookBuilder/prod/data/20120131/FRCP/356"
   * 
   * @param jobExecutionContext
   * @param jobInstance
   * @param bookDefinition
   */
  private void createWorkDir(ExecutionContext jobExecutionContext, JobInstance jobInstance,
      BookDefinition bookDefinition) throws IllegalStateException {
    String dateStr = new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(new Date());
    String dynamicPath = String.format("%s/%s/%s/%s/%d", environmentName, CoreConstants.DATA_DIR, dateStr,
        bookDefinition.getTitleId(), jobInstance.getId());
    File workDirectory = new File(rootWorkDirectory, dynamicPath);
    workDirectory.mkdirs();
    if (!workDirectory.exists()) {
      throw new IllegalStateException(
          "Expected work directory was not created in the filesystem: " + workDirectory.getAbsolutePath());
    }
    jobExecutionContext.putString(JobExecutionKey.WORK_DIRECTORY, workDirectory.getAbsolutePath());
    LOG.debug("workDirectory: " + workDirectory.getAbsolutePath());
  }

  private void createPublishingStats(JobInstance jobInstance, JobParameters jobParams, PublishingStatus publishStatus) {
    PublishingStats pubStats = new PublishingStats();
    Date rightNow = new Date();
    Long ebookDefId = jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID);
    pubStats.setEbookDefId(ebookDefId);
    Long auditId = eBookAuditService.findEbookAuditByEbookDefId(ebookDefId);
    EbookAudit audit = new EbookAudit();
    audit.setAuditId(auditId);
    pubStats.setAudit(audit);
    pubStats.setBookVersionSubmitted(jobParams.getString(JobParameterKey.BOOK_VERSION_SUBMITTED));
    pubStats.setJobHostName(jobParams.getString(JobParameterKey.HOST_NAME));
    pubStats.setJobInstanceId(Long.valueOf(jobInstance.getId().toString()));
    pubStats.setJobSubmitterName(jobParams.getString(JobParameterKey.USER_NAME));
    pubStats.setJobSubmitTimestamp(jobParams.getDate(JobParameterKey.TIMESTAMP));
    pubStats.setPublishStatus("initializeXPPJob : " + publishStatus);
    pubStats.setPublishStartTimestamp(rightNow);
    pubStats.setLastUpdated(rightNow);
    publishingStatsService.savePublishingStats(pubStats);
  }

  @Required
  public void setRootWorkDirectory(File rootDir) {
    this.rootWorkDirectory = rootDir;
  }

  @Required
  public void setEnvironmentName(String envName) {
    this.environmentName = envName;
  }

  @Required
  public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
    this.publishingStatsService = publishingStatsService;
  }

  @Required
  public void setEbookAuditService(EBookAuditService eBookAuditService) {
    this.eBookAuditService = eBookAuditService;
  }

  @Required
  public void setBookDefnService(BookDefinitionService bookDefnService) {
    this.bookDefnService = bookDefnService;
  }
}
