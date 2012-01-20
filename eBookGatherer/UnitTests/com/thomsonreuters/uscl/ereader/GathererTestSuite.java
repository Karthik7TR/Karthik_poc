/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.gather.controller.TocControllerTest;
import com.thomsonreuters.uscl.ereader.gather.services.DocServiceTest;
import com.thomsonreuters.uscl.ereader.gather.services.TocServiceTest;
import com.thomsonreuters.uscl.ereader.gather.util.EBookTocXmlHelper;

@RunWith(Suite.class)
@SuiteClasses( {
			TocControllerTest.class,
			DocServiceTest.class,
			TocServiceTest.class,
			EBookTocXmlHelper.class
		} )

public class GathererTestSuite {

}
