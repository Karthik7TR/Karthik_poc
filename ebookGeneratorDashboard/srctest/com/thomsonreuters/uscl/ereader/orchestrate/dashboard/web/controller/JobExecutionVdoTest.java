/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

import com.thomsonreuters.uscl.ereader.JobParameterKey;

/**
 * Unit tests for the JobExecutionVdo (view data object) which is used on the 
 * Job Summary and Job Execution Details pages as a view model object in the JSP's.
 */
public class JobExecutionVdoTest {

	private static final String BOOK_TITLE_ID_VALUE = "theBookTitleId";
	private static final String BOOK_NAME_VALUE = "theBookName";
	private JobExecutionVdo vdo;
	private JobParameters jobParameters;
	private JobInstance mockJobInstance;
	private JobExecution mockJobExecution;
	
    @Before
    public void setUp() {
    	Map<String,JobParameter> paramMap = new HashMap<String,JobParameter>();
    	paramMap.put(JobParameterKey.BOOK_TITLE_ID, new JobParameter(BOOK_TITLE_ID_VALUE));
    	paramMap.put(JobParameterKey.BOOK_NAME, new JobParameter(BOOK_NAME_VALUE));
    	this.jobParameters = new JobParameters(paramMap);
    	this.mockJobInstance = EasyMock.createMock(JobInstance.class);
    	this.mockJobExecution = EasyMock.createMock(JobExecution.class);
    	
    	this.vdo = new JobExecutionVdo(mockJobExecution);
    }
    
    @Test
    public void testGetBookId() {
    	EasyMock.expect(mockJobExecution.getJobInstance()).andReturn(mockJobInstance);
    	EasyMock.expect(mockJobInstance.getJobParameters()).andReturn(jobParameters);
    	EasyMock.replay(mockJobExecution);
    	EasyMock.replay(mockJobInstance);
    	Assert.assertEquals(BOOK_TITLE_ID_VALUE, vdo.getBookTitleId());
    	EasyMock.verify(mockJobExecution);
    	EasyMock.verify(mockJobInstance);
    }
    @Test
    public void testGetBookTitle() {
    	EasyMock.expect(mockJobExecution.getJobInstance()).andReturn(mockJobInstance);
    	EasyMock.expect(mockJobInstance.getJobParameters()).andReturn(jobParameters);
    	EasyMock.replay(mockJobExecution);
    	EasyMock.replay(mockJobInstance);
    	
    	this.vdo = new JobExecutionVdo(mockJobExecution);
    	Assert.assertEquals(BOOK_NAME_VALUE, vdo.getBookName());
    	EasyMock.verify(mockJobExecution);
    	EasyMock.verify(mockJobInstance);
    }
    
    @Test
    public void testGetSteps() {
    	Collection<StepExecution> stepColl = new HashSet<StepExecution>();
    	JobExecution localJobExecution = new JobExecution(1234l);
    	for (int i=0; i<10; i++) {
    		StepExecution se = new StepExecution("step2", localJobExecution);
    		se.setStartTime(new Date(RandomUtils.nextInt()));
    		stepColl.add(se);
    	}
    	EasyMock.expect(mockJobExecution.getStepExecutions()).andReturn(stepColl);
    	EasyMock.replay(mockJobExecution);
    	
    	List<StepExecution> stepList = vdo.getSteps();
    	Assert.assertTrue(stepList.size() > 0);
    	Assert.assertEquals(stepColl.size(), stepList.size());
    	StepExecution last = null;
    	for (StepExecution current : stepList) {
    		if (last != null) {
    			Assert.assertTrue(current.getStartTime().before(last.getStartTime()));
    		}
    		last = current;
    	}
    	EasyMock.verify(mockJobExecution);
    }
    
    @Test
    public void testIsRestartableWhenStopped() {
    	EasyMock.expect(mockJobExecution.getStatus()).andReturn(BatchStatus.STOPPED);
    	EasyMock.replay(mockJobExecution);
    	Assert.assertTrue(vdo.isRestartable());
    	EasyMock.verify(mockJobExecution);
    }
    @Test
    public void testIsRestartableWhenFailed() {
    	EasyMock.expect(mockJobExecution.getStatus()).andReturn(BatchStatus.FAILED);
    	EasyMock.replay(mockJobExecution);
    	Assert.assertTrue(vdo.isRestartable());
    	EasyMock.verify(mockJobExecution);
    }
    @Test
    public void testGetExecutionDurationMs() {
    	long startMs = 100;
    	long endMs = 24500;
    	long expectedDuration = endMs - startMs;
    	EasyMock.expect(mockJobExecution.getStartTime()).andReturn(new Date(startMs));
    	EasyMock.expect(mockJobExecution.getEndTime()).andReturn(new Date(endMs));
    	EasyMock.replay(mockJobExecution);
    	long actualDuration = vdo.getExecutionDurationMs();
    	Assert.assertEquals(expectedDuration, actualDuration);
    	
    	String durationString = JobExecutionVdo.getExecutionDuration(expectedDuration);
    	Assert.assertEquals("00:00:24.400", durationString);
    	EasyMock.verify(mockJobExecution);
    }
}
