package com.thomsonreuters.uscl.ereader.core.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.dao.JobDaoImpl;
import com.thomsonreuters.uscl.ereader.core.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.domain.JobParameterFilter;

public class JobServiceImpl implements JobService {
	
	private static final Logger log = Logger.getLogger(JobServiceImpl.class);
	public static final int MAX_JOB_EXECUTIONS = 1000;
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
	public List<JobExecution> findJobExecutions(JobFilter jobFilter, JobParameterFilter paramFilter) {
		// Get those executions that match the job filter
		List<Long> jobExecutionIds = findJobExecutionIds(jobFilter);
log.debug("jobExecution ID count="+jobExecutionIds.size());		
		List<JobExecution> executions = findJobExecutions(jobExecutionIds);
log.debug("executions.size="+executions.size());		
		for (JobExecution execution : executions) {
System.out.print(".");
			// Include those executions that match the job parameter filter
			if (paramFilter.matches(execution)) {
				executions.add(execution);
			}
			// Limit the result size
			if (executions.size() >= MAX_JOB_EXECUTIONS) {
				break;
			}
		}
		return executions;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> findJobExecutionIds(JobFilter jobFilter) {
		return dao.findJobExecutionIds(jobFilter);
	}
	
	public boolean isJobRunning(String titleId) {
// TODO: implement this
		return false;
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<JobExecutionEntity> findJobExecutions(JobFilter jobFilter, JobParameterFilter paramFilter) {
//		return dao.findJobExecutions(jobFilter, paramFilter);
//	}
	
	@Required
	public void setJobDao(JobDao dao) {
		this.dao = dao;
	}
	@Required
	public void setJobExplorer(JobExplorer explorer) {
		this.jobExplorer = explorer;
	}
}
