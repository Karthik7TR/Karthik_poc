/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.service;


import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.domain.JobExecutionEntity;
import com.thomsonreuters.uscl.ereader.core.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.domain.JobParameterFilter;
import com.thomsonreuters.uscl.ereader.core.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.domain.JobSort.SortProperty;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class JobServiceIntegrationTest  {
	private static final Logger log = Logger.getLogger(JobServiceIntegrationTest.class);
	@Autowired
	private JobService service;
	
	
	@Test
	public void testFindJobExecutions() {
		JobFilter jobFilter = new JobFilter();
		JobParameterFilter paramFilter = new JobParameterFilter();
		paramFilter.setTitleId("FRCP");
//		JobSort jobSortInfo = new JobSort(SortProperty.executionDuration, true);
		List<JobExecution> executions = service.findJobExecutions(jobFilter, paramFilter);
		Assert.assertTrue(executions.size() > 0);
		log.debug("size="+executions.size());
		
		for (JobExecution exec : executions) {
			Assert.assertNotNull(exec);
			//log.debug(exec);
			JobInstance inst = exec.getJobInstance();
			JobParameters params = inst.getJobParameters();
			//log.debug(exec.getId() + "," + params.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED));
		}
	}
	
//	@Test
//	public void testFetchJobExecutions() {
//		
//		int pageNumber = 1;
//		int expectedItems = 20;
//		JobFilter filter = new JobFilter();
//		SortAndPage sap = new SortAndPage("titleId", true, pageNumber, expectedItems);
//		List<JobExecutionEntity> jobExecs = service.findJobExecutions(filter, sap);
//		Assert.assertNotNull(jobExecs);
//		Assert.assertEquals(expectedItems, jobExecs.size());
//		System.out.println(jobExecs);
//	}
}
