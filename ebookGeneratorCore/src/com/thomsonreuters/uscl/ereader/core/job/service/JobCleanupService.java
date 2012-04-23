/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

/**
 * 
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;

public interface JobCleanupService {

	public void cleanUpDeadJobs();

	public ArrayList<String> findListOfDeadJobs();

}
