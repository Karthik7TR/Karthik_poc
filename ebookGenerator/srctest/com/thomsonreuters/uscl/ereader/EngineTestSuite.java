package com.thomsonreuters.uscl.ereader;

import com.thomsonreuters.uscl.ereader.assemble.service.EbookAssemblyServiceTest;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewExceptionTest;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClientImplTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLAnchorFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLClassAttributeFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLEditorNotesFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLEmptyHeading2FilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLImageFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLInputFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.ProcessingInstructionZapperFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.TitleXMLTOCFilterTest;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XMLImageTagHandlerTest;
import com.thomsonreuters.uscl.ereader.format.service.HTMLWrapperServiceTest;
import com.thomsonreuters.uscl.ereader.format.service.TransformerServiceTest;
import com.thomsonreuters.uscl.ereader.format.service.XMLImageParserServiceTest;
import com.thomsonreuters.uscl.ereader.format.service.XSLTMapperServiceTest;
import com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapterTest;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter.NortNodeFilterTest;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler.NovusDocFileParserTest;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler.NovusNortFileParserTest;
import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDaoTest;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceTest;
import com.thomsonreuters.uscl.ereader.gather.service.NovusDocFileServiceTest;
import com.thomsonreuters.uscl.ereader.gather.service.NovusNortFileServiceTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.EngineServiceTest;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller.OperationsControllerTest;
import com.thomsonreuters.uscl.ereader.proview.rest.CloseableAuthenticationHttpClientFactoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    CloseableAuthenticationHttpClientFactoryTest.class,
    EbookAssemblyServiceTest.class,
    EngineServiceTest.class,
    NovusDocFileServiceTest.class,
    NovusNortFileServiceTest.class,
    NovusNortFileParserTest.class,
    NortNodeFilterTest.class,
    NovusDocFileParserTest.class,
    //Format tests
    HTMLWrapperServiceTest.class,
    TransformerServiceTest.class,
    XMLImageParserServiceTest.class,
    XSLTMapperServiceTest.class,
    HTMLAnchorFilterTest.class,
    HTMLClassAttributeFilterTest.class,
    HTMLEmptyHeading2FilterTest.class,
    HTMLImageFilterTest.class,
    HTMLInputFilterTest.class,
    OperationsControllerTest.class,
    ProcessingInstructionZapperFilterTest.class,
    TitleXMLTOCFilterTest.class,
    XMLImageTagHandlerTest.class,
    DocumentExtensionAdapterTest.class,
    HTMLEditorNotesFilterTest.class,
    //
    ImageDaoTest.class,
    ImageServiceTest.class,
    InitializeTaskTest.class,
    ProviewClientImplTest.class,
    ProviewExceptionTest.class})

public class EngineTestSuite {
    //Intentionally left blank
}
