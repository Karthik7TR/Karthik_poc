package com.thomsonreuters.uscl.ereader;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobQueueManagerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobRunQueuePollerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineServiceTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.OperationsControllerTest;


@RunWith(Suite.class)
@SuiteClasses( { JobQueueManagerTest.class,
				 EngineServiceTest.class,
				 OperationsControllerTest.class,
				 JobRunQueuePollerTest.class,
				 InitializeTaskTest.class
				} )

public class EngineTestSuite {

}
