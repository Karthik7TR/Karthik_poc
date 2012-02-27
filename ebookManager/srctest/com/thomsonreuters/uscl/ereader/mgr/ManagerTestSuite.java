/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.BookLibraryControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BookPromotionControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.GenerateEbookControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.SecurityControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.ViewBookDefinitionControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListControllerTest;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.job.list.JobListFilterControllerTest;

@RunWith(Suite.class)
@SuiteClasses( {
			BookLibraryControllerTest.class,
			BookPromotionControllerTest.class,
			JobListControllerTest.class,
			JobListFilterControllerTest.class,
			GenerateEbookControllerTest.class,
			SecurityControllerTest.class,
			ViewBookDefinitionControllerTest.class
		} )

public class ManagerTestSuite {

}
