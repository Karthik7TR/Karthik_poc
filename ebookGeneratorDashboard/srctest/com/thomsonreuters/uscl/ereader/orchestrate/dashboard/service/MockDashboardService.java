/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.service.DashboardService;

@SuppressWarnings("unchecked")
public class MockDashboardService implements DashboardService {

	@Override
	public List<JobExecution> findAllJobExecutions(String jobName) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<Long> findJobExecutionIds(String jobName, Date startTime,
			BatchStatus batchStatus) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<JobExecution> findJobExecutionByPrimaryKey(
			List<Long> executionIds) {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void jobCleaner(Date jobsBefore) {

	}

	@Override
	public Map<String, String> getBookCodes() {
		return Collections.EMPTY_MAP;
	}

	@Override
	public String getBookTitle(String bookCode) {
		return "Mock Book Title";
	}
}
