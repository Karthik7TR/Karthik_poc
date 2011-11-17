package com.thomsonreuters.uscl.ereader.orchestrate;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionControllerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobInstanceControllerTest;

@RunWith(Suite.class)
@SuiteClasses( { JobExecutionControllerTest.class, JobInstanceControllerTest.class } )

public class DashboardTestSuite {

}
