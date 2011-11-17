package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
		return null;
	}

	@Override
	public StepExecution getStepExecution(Long arg0, Long arg1) {
		return null;
	}
}
