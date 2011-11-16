package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.dao.DashboardDao;

@Component("dashboardService")
public class DashboardServiceImpl implements DashboardService {
	//private static final Logger log = Logger.getLogger(DashboardServiceImpl.class);
	private static final int MAX_JOB_INSTANCES = 1000;
	
	@Autowired
	private DashboardDao dao;
	@Autowired
	private JobExplorer jobExplorer;
	
	@Override
	@Transactional(readOnly = true)
	public List<JobExecution> findAllJobExecutions(String jobName) {
		List<JobExecution> allJobExecutions = new ArrayList<JobExecution>();
		if (StringUtils.isNotBlank(jobName)) {
			List<JobInstance> jobInstances = jobExplorer.getJobInstances(jobName,  0, MAX_JOB_INSTANCES);
			for (JobInstance jobInstance : jobInstances) {
				List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
				allJobExecutions.addAll(jobExecutions);
			}
		}
		return allJobExecutions;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Long> findJobExecutionIds(String jobName, JobExecution filter) {
		return dao.findJobExecutionIds(jobName, filter);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<JobExecution> findJobExecutionByPrimaryKey(List<Long> executionIds) {
		List<JobExecution> jobExecutions = new ArrayList<JobExecution>();
		for (Long executionId : executionIds) {
			JobExecution jobExecution = jobExplorer.getJobExecution(executionId);
			if (jobExecution != null) {
				jobExecutions.add(jobExecution);
			}
		}
		return jobExecutions;
	}
	
	@Override
	@Transactional
	public void jobCleaner(Date jobsBefore) {
		dao.deleteJobsBefore(jobsBefore);
	}
}
