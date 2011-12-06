package com.thomsonreuters.uscl.ereader.orchestrate.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "core-test-context.xml" } )
public class JobRunnerTest  {
	private static final String BOOK_CODE = "junitBook";
	private static final String BOOK_TITLE = "Test Tile for Book Test";
	private static final String BOOK_VERSION = "999";
	@Autowired
	private JobRunner jobRunner;
	
	@Before
	public void setUp() {
		Assert.assertNotNull(jobRunner);
	}
	
	@Test
	public void testHighPriorityJobRunRequest() {
		try {
			JobRunRequest jobRunRequest = JobRunRequest.create(BOOK_CODE, BOOK_TITLE, BOOK_VERSION, "joeh", "joeh@bogus.com");
			jobRunner.enqueueHighPriorityJobRunRequest(jobRunRequest);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testNormalPriorityJobRunRequest() {
		try {
			JobRunRequest jobRunRequest = JobRunRequest.create(BOOK_CODE, BOOK_TITLE, BOOK_VERSION, "joen", "joen@bogus.com");
			jobRunner.enqueueNormalPriorityJobRunRequest(jobRunRequest);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
