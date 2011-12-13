/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequestTest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunnerTest;
@RunWith(Suite.class)
@SuiteClasses( { JobRunRequestTest.class, JobRunnerTest.class } )
public class CoreTestSuite {

}
