package com.thomsonreuters.uscl.ereader.orchestrate.engine;

/**
 * Thrown when we want to indicate that a job should be stopped as urgently as possible within the business logic
 * of a step.
 */
public class StopJobException extends Exception {
	private static final long serialVersionUID = -5155270391841263172L;

	public StopJobException(String mesg) {
		super(mesg);
	}
}
