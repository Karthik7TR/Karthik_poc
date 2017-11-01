package com.thomsonreuters.uscl.ereader;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDaoTest;
import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDaoTest;
import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDaoTest;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionTest;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionServiceTest;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeServiceTest;
import com.thomsonreuters.uscl.ereader.core.book.service.EbookAuditServiceTest;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutageTest;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageProcessorTest;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageServiceTest;
import com.thomsonreuters.uscl.ereader.core.service.CoreServiceTest;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImplTest;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilterTest;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelperTest;
import com.thomsonreuters.uscl.ereader.smoketest.dao.SmokeTestDaoTest;
import com.thomsonreuters.uscl.ereader.smoketest.domain.SmokeTestTest;
import com.thomsonreuters.uscl.ereader.userpreference.dao.UserPreferenceDaoTest;
import com.thomsonreuters.uscl.ereader.userpreference.domain.UserPreferenceTest;
import com.thomsonreuters.uscl.ereader.userpreference.service.UserPreferenceServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    BookDefinitionTest.class,
    BookDefinitionDaoTest.class,
    BookDefinitionServiceTest.class,
    FileExtensionFilterTest.class,
    FileHandlingHelperTest.class,
    com.thomsonreuters.uscl.ereader.jaxb.JAXBMarshallingTest.class,
    CodeDaoTest.class,
    CodeServiceTest.class,
    CoreServiceTest.class,
    ProviewClientImplTest.class,
    EbookAuditDaoTest.class,
    OutageProcessorTest.class,
    OutageServiceTest.class,
    PlannedOutageTest.class,
    EbookAuditServiceTest.class,
    SmokeTestTest.class,
    SmokeTestDaoTest.class,
    UserPreferenceDaoTest.class,
    UserPreferenceServiceTest.class,
    UserPreferenceTest.class})
public class CoreTestSuite {
    //Intentionally left blank
}
