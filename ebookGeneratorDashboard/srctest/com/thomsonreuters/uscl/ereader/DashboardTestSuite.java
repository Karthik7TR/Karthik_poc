/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.CreateBookControllerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionControllerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionVdoTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobInstanceControllerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobSummaryControllerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.StepExecutionControllerTest;

@RunWith(Suite.class)
@SuiteClasses( { JobExecutionControllerTest.class, 
				 JobInstanceControllerTest.class,
				 JobSummaryControllerTest.class,
				 StepExecutionControllerTest.class,
				 CreateBookControllerTest.class,
				 JobExecutionVdoTest.class} )

public class DashboardTestSuite {

}
