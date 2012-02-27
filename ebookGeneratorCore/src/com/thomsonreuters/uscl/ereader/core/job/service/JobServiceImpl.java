package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;

public class JobServiceImpl implements JobService {
	
	//private static final Logger log = Logger.getLogger(JobServiceImpl.class);
	private JobDao dao;
	private JobExplorer jobExplorer;
	
	
	@Override
	@Transactional(readOnly = true)
	public JobExecution findJobExecution(Long jobExecutionId) {
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
	@Transactional(readOnly = true)
	public List<Long> findJobExecutions(JobFilter filter, JobSort sort) {
		return dao.findJobExecutions(filter, sort);
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
}
