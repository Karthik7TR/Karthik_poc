package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobInstanceBookInfo;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class JobServiceImpl implements JobService {
	
	//private static final Logger log = Logger.getLogger(JobServiceImpl.class);
	private JobDao dao;
	private JobExplorer jobExplorer;
	private PublishingStatsService publishingStatsService;
	
	
	@Override
	@Transactional(readOnly = true)
	public JobExecution findJobExecution(long jobExecutionId) {
		return jobExplorer.getJobExecution(jobExecutionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<JobExecution> findJobExecutions(List<Long> jobExecutionIds) {
		List<JobExecution> jobExecutions = new ArrayList<JobExecution>();
		for (Long jobExecutionId : jobExecutionIds) {
			JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
			if (jobExecution != null) {
				jobExecutions.add(jobExecution);
			}
		}
		return jobExecutions;
	}
	
	@Override
	public List<JobExecution> findJobExecutions(JobInstance jobInstance) {
		List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
		return jobExecutions;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort) {
		return dao.findJobExecutions(filter, sort);
	}
	
	@Override
	public JobInstance findJobInstance(long jobInstanceId) {
		return jobExplorer.getJobInstance(jobInstanceId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public JobInstanceBookInfo findJobInstanceBookInfo(long jobInstanceId) {
		EbookAudit audit = publishingStatsService.findAuditInfoByJobId(jobInstanceId);
		if (audit == null) {
			audit = new EbookAudit();
		}
		JobInstanceBookInfo bookInfo = new JobInstanceBookInfo(audit.getBookNamesConcat(), audit.getTitleId());
		return bookInfo;
	}
	
	@Override
	public StepExecution findStepExecution(long jobExecutionId, long stepExecutionId) {
		StepExecution stepExecution = jobExplorer.getStepExecution(jobExecutionId, stepExecutionId);
		return stepExecution;
	}
	
	public boolean isJobRunning(String titleId) {
// TODO: implement this
		return false;
	}
	
	@Required
	public void setJobDao(JobDao dao) {
		this.dao = dao;
	}
	@Required
	public void setJobExplorer(JobExplorer explorer) {
		this.jobExplorer = explorer;
	}
	@Required
	public void setPublishingStatsService(PublishingStatsService service) {
		this.publishingStatsService = service;
	}
}
