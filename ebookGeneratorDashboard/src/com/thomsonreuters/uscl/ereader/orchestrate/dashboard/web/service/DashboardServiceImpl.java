package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.dao.DashboardDao;

public class DashboardServiceImpl implements DashboardService {
	//private static final Logger log = Logger.getLogger(DashboardServiceImpl.class);
	private static final int MAX_JOB_INSTANCES = 1000;
	private static Map<String,String> bookMap = new HashMap<String,String>();
// TODO: Implement this ... fetch the book id=title lookup
	static {
		bookMap.put("FL_2011_LOCAL", "Flordia's Rules of court 2011");
		bookMap.put("DC_2011_FOO", "TODO: Guide to Foo & other stuff!");
		bookMap.put("DC_2011_BAR", "TODO: Advanced Bar & stuff!");
	}

	private DashboardDao dao;
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
	public List<Long> findJobExecutionIds(String jobName, Date startTime, BatchStatus batchStatus) {
		return dao.findJobExecutionIds(jobName, startTime, batchStatus);
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
	
	@Override
	public Map<String,String> getBookCodes() {	// TODO: implement this
		return bookMap;
	}
	@Override
	public String getBookTitle(String bookCode) {
		return bookMap.get(bookCode);
	}
	@Required
	public void setDao(DashboardDao dao) {
		this.dao = dao;
	}
	@Required
	public void setJobExplorer(JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}
}
