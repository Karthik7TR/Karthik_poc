package com.thomsonreuters.uscl.ereader.orchestrate.engine;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobQueueManagerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineServiceTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.OperationsControllerTest;


@RunWith(Suite.class)
@SuiteClasses( { JobQueueManagerTest.class,
				 EngineServiceTest.class,
				 OperationsControllerTest.class
				} )

public class EngineTestSuite {

}
