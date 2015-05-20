package com.thomsonreuters.uscl.ereader.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public class SplitBookDecider implements JobExecutionDecider {

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {

		BookDefinition bookDefinition = (BookDefinition)jobExecution.getExecutionContext().get(JobExecutionKey.EBOOK_DEFINITON);
		if(bookDefinition.isSplitBook()) {
			return new FlowExecutionStatus("SPLIT_BOOK");
		} else {
			return new FlowExecutionStatus("SINGLE_BOOK");
		}
	}

}
