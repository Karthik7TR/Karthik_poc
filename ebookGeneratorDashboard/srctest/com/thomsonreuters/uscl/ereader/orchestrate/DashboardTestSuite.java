package com.thomsonreuters.uscl.ereader.orchestrate;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionControllerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobInstanceControllerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobSummaryControllerTest;

@RunWith(Suite.class)
@SuiteClasses( { JobExecutionControllerTest.class, JobInstanceControllerTest.class,
				 JobSummaryControllerTest.class } )

public class DashboardTestSuite {

}
