package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.batch.core.JobExecution;

public class MockDashboardDao implements DashboardDao {

	@Override
	public List<Long> findJobExecutionIds(String jobName, JobExecution filter) {
		return new ArrayList<Long>(0);
	}

	@Override
	public void deleteJobsBefore(Date jobsBefore) {

	}
}
