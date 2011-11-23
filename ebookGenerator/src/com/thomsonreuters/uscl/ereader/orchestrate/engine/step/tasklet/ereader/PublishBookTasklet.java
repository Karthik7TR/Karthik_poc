package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.tasklet.ereader;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

public class PublishBookTasklet extends AbstractSbTasklet {
	private static final Logger log = Logger.getLogger(PublishBookTasklet.class);

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		return ExitStatus.COMPLETED;
	}
}
