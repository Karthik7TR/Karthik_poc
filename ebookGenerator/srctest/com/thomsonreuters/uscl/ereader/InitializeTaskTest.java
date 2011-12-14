package com.thomsonreuters.uscl.ereader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

public class InitializeTaskTest {
	
	private InitializeTask task;
	private StepContribution stepContrib;
	private ChunkContext chunkContext;
	private StepContext stepContext;
	private Map<String,Object> jobParameterMap;
	private StepExecution stepExecution;
	private JobExecution jobExecution;
	private ExecutionContext jobExecutionContext;
	
	@Before
	public void setUp() {
		this.stepContrib = EasyMock.createMock(StepContribution.class);
		this.chunkContext = EasyMock.createMock(ChunkContext.class);
		this.stepContext = EasyMock.createMock(StepContext.class);
		this.jobParameterMap = new HashMap<String,Object>();
		this.stepExecution = EasyMock.createMock(StepExecution.class);
		this.jobExecution = EasyMock.createMock(JobExecution.class);
		this.jobExecutionContext = new ExecutionContext();
		this.task = new InitializeTask();
	}
	
	@Test
	public void testExecuteStep() {
		EasyMock.expect(chunkContext.getStepContext()).andReturn(stepContext);
		EasyMock.expect(stepContext.getJobParameters()).andReturn(jobParameterMap);
		EasyMock.expect(stepContext.getStepExecution()).andReturn(stepExecution);
		EasyMock.expect(stepExecution.getJobExecution()).andReturn(jobExecution);
		EasyMock.expect(jobExecution.getExecutionContext()).andReturn(jobExecutionContext);
		EasyMock.replay(chunkContext);
		EasyMock.replay(stepContext);
		EasyMock.replay(stepExecution);
		EasyMock.replay(jobExecution);
		try {
			ExitStatus transition = task.executeStep(stepContrib, chunkContext);
			// Verify the root directory for this ebook
			File expectedRoot = new File("/nas/ebookbuilder/data/samplebook"); // TODO: eventually a function of job parameters
			File actualRoot = new File(jobExecutionContext.getString(JobExecutionKey.EBOOK_DIRECTORY_PATH));
			Assert.assertEquals(expectedRoot.getAbsolutePath(), actualRoot.getAbsolutePath());
			// Verify the golden book absolute path
			File expectedFile = new File(expectedRoot.getParentFile(), "TODO_BOOK_TITLE_ID.gz");	// TODO: eventually a function of job parameters and book title id
			File actualFile = new File(jobExecutionContext.getString(JobExecutionKey.EBOOK_FILE_PATH));
			Assert.assertEquals(expectedFile.getAbsolutePath(), actualFile.getAbsolutePath());
			// Verify the transition to the next step
			Assert.assertEquals(ExitStatus.COMPLETED, transition);
			// Verify all collaborators were called as expected
			EasyMock.verify(chunkContext);
			EasyMock.verify(stepContext);
			EasyMock.verify(stepExecution);
			EasyMock.verify(jobExecution);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
