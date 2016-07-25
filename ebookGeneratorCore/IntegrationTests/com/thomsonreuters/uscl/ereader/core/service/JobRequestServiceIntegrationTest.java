/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.core.service;


import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration 
@Transactional
public class JobRequestServiceIntegrationTest  {
	//private static final Logger log = LogManager.getLogger(JobRequestServiceIntegrationTest.class);

	@Autowired
	private JobRequestService jobRequestService;
	private static final Long BOOK_DEFINITION_PK = new Long(7);
	private static final String BOOK_VERSION = "3.2";
	private static final int PRIORITY = 5;
	private static final String SUBMITTED_BY = "Hans Bethe";
	private static BookDefinition BOOK_DEFINITION;
	
	static {
		BOOK_DEFINITION = new BookDefinition();
		BOOK_DEFINITION.setEbookDefinitionId(BOOK_DEFINITION_PK);
	}
	
	public void setJobRequestservice(JobRequestService jobRequestservice) {
		this.jobRequestService = jobRequestservice;
	}
	
	@Before
	public void setUp() throws Exception {
	}
	
	private Long saveStandardJobRequest() {
		String version = "3.2";
		int priority = 5;
		String submittedBy = "Hans Bethe";
		Long pk = jobRequestService.saveQueuedJobRequest(BOOK_DEFINITION, version, priority, submittedBy);
		return pk;
	}
	
	@Test
	public void testSaveJobRequest() {
		Long pk = saveStandardJobRequest();
		
		Assert.assertNotNull(pk);
		JobRequest actualJobRequest = jobRequestService.findByPrimaryKey(pk);
		Assert.assertEquals(BOOK_DEFINITION_PK, actualJobRequest.getBookDefinition().getEbookDefinitionId());
		Assert.assertEquals(BOOK_VERSION, actualJobRequest.getBookVersion());
		Assert.assertEquals(PRIORITY, actualJobRequest.getPriority());
		Assert.assertEquals(SUBMITTED_BY, actualJobRequest.getSubmittedBy());
		Assert.assertNotNull(actualJobRequest.getBookDefinition());
	}
	
	@Test
	public void testUpdateJobPriority(){
		Long pk = saveStandardJobRequest();
		int newJobPriority = 95;	
		jobRequestService.updateJobPriority(pk, newJobPriority);
		JobRequest found = jobRequestService.findByPrimaryKey(pk);
		Assert.assertEquals(newJobPriority, found.getPriority());
	}

}
