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
	private static final String JOB_NAME = "junitJobName";
	@Autowired
	private JobRunner jobRunner;
	
	@Before
	public void setUp() {
		Assert.assertNotNull(jobRunner);
	}
	
	@Test
	public void testHighPriorityJobRunRequest() {
		try {
			jobRunner.enqueueHighPriorityJobRunRequest(JOB_NAME, Thread.MAX_PRIORITY);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	@Test
	public void testNormalPriorityJobRunRequest() {
		try {
			jobRunner.enqueueNormalPriorityJobRunRequest(JOB_NAME, Thread.MAX_PRIORITY);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
