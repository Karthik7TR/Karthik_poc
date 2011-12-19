package com.thomsonreuters.uscl.ereader;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;

public class InitializeTaskTest {
	
	private static final Long JOB_ID = System.currentTimeMillis();
	private static final String TITLE_ID = "JunitTestTitleId";
	private static final String DATE_STAMP = InitializeTask.DATE_FORMAT.format(new Date());
	private InitializeTask task;
	private StepContribution stepContrib;
	private ChunkContext chunkContext;
	private StepContext stepContext;
	private StepExecution stepExecution;
	private JobExecution jobExecution;
	private JobInstance jobInstance;
	private ExecutionContext jobExecutionContext;
	private File tempRootDir;
	
	@Before
	public void setUp() {
		this.stepContrib = EasyMock.createMock(StepContribution.class);
		this.chunkContext = EasyMock.createMock(ChunkContext.class);
		this.stepContext = EasyMock.createMock(StepContext.class);
		this.stepExecution = EasyMock.createMock(StepExecution.class);
		this.jobExecution = EasyMock.createMock(JobExecution.class);
		this.jobInstance = EasyMock.createMock(JobInstance.class);
		this.jobExecutionContext = new ExecutionContext();
		this.task = new InitializeTask();
		this.tempRootDir = new File(System.getProperty("java.io.tmpdir"));
System.out.println("Temp dir: " + tempRootDir.getAbsolutePath());  // DEBUG
		task.setRootWorkDirectory(tempRootDir);
	}
	
	@Test
	public void testExecuteStep() {
		Map<String,JobParameter> paramMap = new HashMap<String,JobParameter>();
		paramMap.put(JobParameterKey.TITLE_ID, new JobParameter(TITLE_ID));
		JobParameters jobParams = new JobParameters(paramMap);
		EasyMock.expect(chunkContext.getStepContext()).andReturn(stepContext);
		EasyMock.expect(stepContext.getStepExecution()).andReturn(stepExecution);
		EasyMock.expect(stepExecution.getJobExecution()).andReturn(jobExecution);
		EasyMock.expect(jobExecution.getExecutionContext()).andReturn(jobExecutionContext);
		EasyMock.expect(jobExecution.getJobInstance()).andReturn(jobInstance);
		EasyMock.expect(jobInstance.getJobParameters()).andReturn(jobParams);
		EasyMock.expect(jobInstance.getId()).andReturn(JOB_ID);
		EasyMock.replay(chunkContext);
		EasyMock.replay(stepContext);
		EasyMock.replay(stepExecution);
		EasyMock.replay(jobExecution);
		EasyMock.replay(jobInstance);
		File expectedWorkDirectory = null;
		try {
			ExitStatus transition = task.executeStep(stepContrib, chunkContext);
			// Verify the root directory for this ebook
			String dynamicPath = String.format("%s/%s/%d", DATE_STAMP, TITLE_ID, JOB_ID);
			
			expectedWorkDirectory = new File(tempRootDir, dynamicPath);
			File expectedEbookFile = new File(expectedWorkDirectory, TITLE_ID + InitializeTask.BOOK_FILE_TYPE_SUFFIX);
			
			File actualWorkDirectory = new File(jobExecutionContext.getString(JobExecutionKey.EBOOK_DIRECTORY_PATH));
			File actualEbookFile = new File(jobExecutionContext.getString(JobExecutionKey.EBOOK_FILE_PATH));
			
			Assert.assertEquals(expectedWorkDirectory.getAbsolutePath(), actualWorkDirectory.getAbsolutePath());
			Assert.assertEquals(expectedEbookFile.getAbsolutePath(), actualEbookFile.getAbsolutePath());
			
			// Verify the transition to the next step
			Assert.assertEquals(ExitStatus.COMPLETED, transition);
			// Verify all collaborators were called as expected
			EasyMock.verify(chunkContext);
			EasyMock.verify(stepContext);
			EasyMock.verify(stepExecution);
			EasyMock.verify(jobExecution);
			EasyMock.verify(jobInstance);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			expectedWorkDirectory.delete();
			Assert.assertFalse(expectedWorkDirectory.exists());
		}
	}
}
