package com.thomsonreuters.uscl.ereader.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;

public class NovusOrFileDecider implements JobExecutionDecider {

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		BookDefinition bookDefinition = (BookDefinition)jobExecution.getExecutionContext().get(JobExecutionKey.EBOOK_DEFINITON);
		if(SourceType.FILE.equals(bookDefinition.getSourceType())) {
			return new FlowExecutionStatus("NAS_FILE");
		} else {
			return new FlowExecutionStatus("NOVUS");
		}
	}

}
