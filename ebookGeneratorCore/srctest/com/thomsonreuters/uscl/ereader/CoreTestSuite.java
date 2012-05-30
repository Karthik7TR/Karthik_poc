/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoTest;
import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDaoTest;
import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDaoTest;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionTest;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceTest;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeServiceTest;
import com.thomsonreuters.uscl.ereader.core.book.service.EbookAuditServiceTest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobServiceTest;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageTest;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageServiceTest;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImplTest;
import com.thomsonreuters.uscl.ereader.gather.domain.JibxMarshallingTest;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilterTest;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelperTest;
import com.thomsonreuters.uscl.ereader.smoketest.dao.SmokeTestDaoTest;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTestTest;
import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDaoTest;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreferenceTest;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceServiceTest;

@RunWith(Suite.class)
@SuiteClasses({ BookDefinitionTest.class, BookDefinitionDaoTest.class,
		BookDefinitionServiceTest.class, FileExtensionFilterTest.class,
		FileHandlingHelperTest.class, JibxMarshallingTest.class,
		JobServiceTest.class, CodeDaoTest.class, CodeServiceTest.class,
		ProviewClientImplTest.class, EbookAuditDaoTest.class, OutageServiceTest.class,
		EbookAuditServiceTest.class, SmokeTestTest.class, SmokeTestDaoTest.class,
		UserPreferenceDaoTest.class, UserPreferenceServiceTest.class, UserPreferenceTest.class,
		PlannedOutageTest.class

})
public class CoreTestSuite {

}
