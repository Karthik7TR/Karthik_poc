/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

/**
 * @author Mahendra Survase (u0105927)
 *
 */
public interface JobStartupThrottleService {

	public boolean checkIfnewJobCanbeLaunched();

}
