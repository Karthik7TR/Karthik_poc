package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service.DashboardService;

public class MockDashboardService implements DashboardService {

	@Override
	public List<JobExecution> findAllJobExecutions(String jobName) {
		return new ArrayList<JobExecution>(0);
	}

	@Override
	public List<Long> findJobExecutionIds(String jobName, JobExecution filter) {
		return new ArrayList<Long>(0);
	}

	@Override
	public List<JobExecution> findJobExecutionByPrimaryKey(
			List<Long> executionIds) {
		return new ArrayList<JobExecution>(0);
	}

	@Override
	public void jobCleaner(Date jobsBefore) {
	}
}
