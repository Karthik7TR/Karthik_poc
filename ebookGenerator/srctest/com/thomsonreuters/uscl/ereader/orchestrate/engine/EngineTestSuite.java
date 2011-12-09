package com.thomsonreuters.uscl.ereader.orchestrate.engine;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobQueueManagerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineServiceTest;


@RunWith(Suite.class)
@SuiteClasses( { JobQueueManagerTest.class,
				 EngineServiceTest.class} )

public class EngineTestSuite {

}
