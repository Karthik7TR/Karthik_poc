package com.thomsonreuters.uscl.ereader.orchestrate.dashboard;

import java.util.List;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;

public class MockJobExplorer implements JobExplorer {

	public MockJobExplorer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<JobExecution> findRunningJobExecutions(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobExecution getJobExecution(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobExecution> getJobExecutions(JobInstance arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobInstance getJobInstance(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobInstance> getJobInstances(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getJobNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StepExecution getStepExecution(Long arg0, Long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
