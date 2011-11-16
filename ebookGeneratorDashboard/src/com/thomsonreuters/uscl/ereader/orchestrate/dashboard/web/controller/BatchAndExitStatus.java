package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;

public class BatchAndExitStatus {

	private BatchStatus batchStatus;
	private ExitStatus exitStatus;
	
	public BatchAndExitStatus (BatchStatus batch, ExitStatus exit) {
		this.batchStatus = batch;
		this.exitStatus = exit;
	}
	
	public BatchStatus getBatchStatus() {
		return batchStatus;
	}
	public ExitStatus getExitStatus() {
		return exitStatus;
	}
}
