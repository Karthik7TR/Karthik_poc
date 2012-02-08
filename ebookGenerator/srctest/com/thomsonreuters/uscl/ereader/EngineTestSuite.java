/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.thomsonreuters.uscl.ereader.assemble.service.EbookAssemblyServiceTest;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewExceptionTest;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImplTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLAnchorFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLClassAttributeFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLImageFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLInputFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.ProcessingInstructionZapperFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.TitleXMLTOCFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XMLImageTagHandlerTest;
import com.thomsonreuters.uscl.ereader.format.service.HTMLWrapperServiceTest;
import com.thomsonreuters.uscl.ereader.format.service.TransformerServiceTest;
import com.thomsonreuters.uscl.ereader.format.service.XMLImageParserServiceTest;
import com.thomsonreuters.uscl.ereader.format.service.XSLTMapperServiceTest;
import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDaoTest;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceTest;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageVerticalJsonTest;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataServiceTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobQueueManagerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.queue.JobRunQueuePollerTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineServiceTest;
import com.thomsonreuters.uscl.ereader.proview.rest.BasicAuthenticationHttpClientFactoryTest;

@RunWith(Suite.class)
@SuiteClasses( {
			BasicAuthenticationHttpClientFactoryTest.class,
			DocMetadataServiceTest.class,
			EbookAssemblyServiceTest.class,
			EngineServiceTest.class,
			//Format tests
			HTMLWrapperServiceTest.class,
			TransformerServiceTest.class,
			XMLImageParserServiceTest.class,
			XSLTMapperServiceTest.class,
			HTMLAnchorFilterTest.class,
			HTMLClassAttributeFilterTest.class,
			HTMLImageFilterTest.class,
			HTMLInputFilterTest.class,
			ProcessingInstructionZapperFilterTest.class,
			TitleXMLTOCFilterTest.class,
			XMLImageTagHandlerTest.class,
			//
			ImageDaoTest.class,
			ImageServiceTest.class,
			ImageVerticalJsonTest.class,
			InitializeTaskTest.class,
			JobQueueManagerTest.class,
			JobRunQueuePollerTest.class,
			ProviewClientImplTest.class,
			ProviewExceptionTest.class
		} )

public class EngineTestSuite {

}
