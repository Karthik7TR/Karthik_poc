package com.thomsonreuters.uscl.ereader.orchestrate.engine.step.item.play;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;


/**
 * Lifecycle callback that allows for notification before a Step is started and after it has ended.
 */
public class PlayStepExecutionListener implements StepExecutionListener {
	private static final Logger log = Logger.getLogger(PlayStepExecutionListener.class);
	
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.debug(">>>");
		return ExitStatus.COMPLETED;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.debug(">>>");
	}
}
