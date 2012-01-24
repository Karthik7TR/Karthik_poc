/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.gather.domain.JibxMarshallingTest;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilterTest;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelperTest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionTest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequestTest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunnerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.dao.CoreDaoTest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreServiceTest;
@RunWith(Suite.class)
@SuiteClasses( { 
				BookDefinitionTest.class,
				CoreDaoTest.class,
				CoreServiceTest.class,
				FileExtensionFilterTest.class,
				FileHandlingHelperTest.class,
				JibxMarshallingTest.class,
				JobRunRequestTest.class,
				JobRunnerTest.class
			} )
public class CoreTestSuite {

}
