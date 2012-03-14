/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.JobStartupThrottleDao;

/**
 * This class covers Throttle behavior for spring batch where before starting new job , jobRepository is queried to find 
 * how many jobs are currently running and what is current throttle limit. if more number of jobs running are more or 
 * equal to throttle Limit. Each running job is verified if they have crossed throttle step then total number of such jobs 
 * which have not crossed throttle limit is considered to decide if new job should be allowed launch.  
 *    
 *  @author Mahendra Survase (u0105927)
 */
public class JobStartupThrottleServiceImpl implements  JobStartupThrottleService{
	
	private static final Logger log = Logger.getLogger(JobStartupThrottleServiceImpl.class);

	
	@Autowired
	public JobExplorer jobExplorer;

	@Autowired
	public JobRepository jobRepository;
	
	@Autowired
	public JobStartupThrottleDao jobStartupThrottleDao;

	@Autowired
	public String throttleStepCheck;




	/**
	 * Before starting new job check with Spring Batch Job Repository to find if new job can be 
	 * launched with out breaking throttle limit.  
	 */
	@Override
	public boolean checkIfnewJobCanbeLaunched(){
		
		int throttleLimit = jobStartupThrottleDao.getThrottleLimitForExecutionStep(throttleStepCheck);
		boolean jobPullFlag= false;
		int jobNotDoneWithKeyStep = 0;
		Set<JobExecution> runningJobExecutions = null;
		List<String> jobNames = jobExplorer.getJobNames();
		 
		if (jobNames != null && jobNames.size() > 0 ) {
			// spring batch is capable of running multiple jobs but in current implementation we have one job with multiple instances.  
			runningJobExecutions = jobExplorer.findRunningJobExecutions(jobExplorer.getJobNames().get(0));
		}
		
		if(runningJobExecutions != null && runningJobExecutions.size() >= throttleLimit )
		{
			//log.debug("********************************************************************************************  jobExecutions.size = "+runningJobExecutions.size());

			// find out how many jobs are done with key steps like Novus... 
			for (JobExecution jobExecution : runningJobExecutions) {
				
				Long jobId = jobExecution.getJobInstance().getId();
				
				JobInstance jobInstance = jobExecution.getJobInstance();
				// retrieve last step of execution 
				StepExecution stepExecution = jobRepository.getLastStepExecution(jobInstance, throttleStepCheck);
				
				if(stepExecution == null){
					
					// job is not done with key step 
					jobNotDoneWithKeyStep ++;
					log.debug("THROTTLE_STEP is not done for jobId ="+ jobId  +"  and book name "+ jobInstance.getJobParameters().getParameters().get("BOOK_NAME"));
					
				}else {
					log.debug("Job is gone beyond THROTTLE_STEP = " +throttleStepCheck);
				}
			}
			
			if(jobNotDoneWithKeyStep < throttleLimit){
				jobPullFlag = true;
			}else{
				jobPullFlag = false;
			}
			
		}else{

			// Safe to start new job as number of jobs currently running is less than throttle limit. 
			jobPullFlag = true;
		}
		//log.debug("******************************************************************************************** jobPullFlag "+jobPullFlag);
		return jobPullFlag;
	}
	
	
	
	@Required
	public void setJobExplorer(JobExplorer jobExplorer) {
		this.jobExplorer = jobExplorer;
	}
	
	@Required
	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}
	
	@Required
	public void setJobStartupThrottleDao(JobStartupThrottleDao jobStartupThrottleDao) {
		this.jobStartupThrottleDao = jobStartupThrottleDao;
	}
	
	@Required
	public void setThrottleStepCheck(String throttleStepCheck) {
		this.throttleStepCheck = throttleStepCheck;
	}

}
