/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader;

import com.thomsonreuters.uscl.ereader.gather.controller.DocControllerTest;
import com.thomsonreuters.uscl.ereader.gather.controller.HomeControllerTest;
import com.thomsonreuters.uscl.ereader.gather.controller.NortControllerTest;
import com.thomsonreuters.uscl.ereader.gather.controller.TocControllerTest;
import com.thomsonreuters.uscl.ereader.gather.parser.NortLabelParserTest;
import com.thomsonreuters.uscl.ereader.gather.services.DocServiceTest;
import com.thomsonreuters.uscl.ereader.gather.services.NortServiceTest;
import com.thomsonreuters.uscl.ereader.gather.services.TocServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    DocControllerTest.class,
    DocServiceTest.class,
    TocControllerTest.class,
    TocServiceTest.class,
    NortControllerTest.class,
    NortServiceTest.class,
    HomeControllerTest.class,
    NortLabelParserTest.class})

public class GathererTestSuite {
    //Intentionally left blank
}
