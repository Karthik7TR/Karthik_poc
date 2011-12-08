/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;

public class MockJobExplorer implements JobExplorer {

	public MockJobExplorer() {
	}

	@Override
	public Set<JobExecution> findRunningJobExecutions(String arg0) {
		return null;
	}

	@Override
	public JobExecution getJobExecution(Long id) {
		return new JobExecution(id);
	}
	@Override 
	public StepExecution getStepExecution(Long jobExecId, Long stepExecId) {
		return new StepExecution("mockStep", EasyMock.createMock(JobExecution.class));
		
	}

	@Override
	public List<JobExecution> getJobExecutions(JobInstance arg0) {
		return new ArrayList<JobExecution>(0);
	}

	@Override
	public JobInstance getJobInstance(Long id) {
		JobParameters jobParameters = new JobParameters();
		return new JobInstance(id, jobParameters, "mockTestingJobName");
	}

	@Override
	public List<JobInstance> getJobInstances(String arg0, int arg1, int arg2) {
		return null;
	}

	@Override
	public List<String> getJobNames() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("fooJob");
		list.add("barJob");
		return list;
	}
}
