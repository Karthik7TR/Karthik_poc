package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Difference;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;

public class SplitTocManifestFilterTest extends TitleMetadataTestBase {
	private static final Logger LOG = Logger.getLogger(SplitTocManifestFilterTest.class);
	UuidGenerator uuidGenerator;
	SAXParserFactory saxParserFactory;
	SplitTocManifestFilter splitTocManifestFilterTest;
	TitleMetadata titleMetadata;
	OutputStream titleManifestOutputStream;
	InputStream tocXml;
	Serializer serializer;
	SAXParser saxParser;
	XMLReader xmlReader;
	ByteArrayOutputStream resultStream;
	FileUtilsFacade mockFileUtilsFacade;
    PlaceholderDocumentService mockPlaceholderDocumentService;
    TitleMetadataServiceImpl titleMetadataService;
    File altIdFile;
    	    
    
	private static final String EXPECTED__SPLIT_TOC = "<toc><titlebreak/><entry s=\"yarr/pirates#FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry><entry s=\"yarr/pirates#Copyright/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text><entry s=\"yarr/pirates#Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry><entry s=\"yarr/pirates#ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry><entry s=\"yarr/pirates#WestlawNext/WestlawNextAnchor\"><text>WestlawNext</text></entry></entry>";	
	private static final String EXPECTED_END_MANIFEST = "</title>";
	File altIdDir;
	
	String lastupdated = new SimpleDateFormat("yyyyMMdd").format(new Date());
	String onlineexpiration = "29991231";
	

	@Rule
	public static TemporaryFolder tempDirectory = new TemporaryFolder();
	File temporaryDirectory;
	Map<String, List<String>> docImageMap;
	
	@Before
	public void setUp() throws Exception {
		
		// Add 2 years to the current date for online expiration
	    Calendar calendar = Calendar.getInstance();		
	    calendar.setTime(new Date());
	    calendar.add(Calendar.YEAR, 2);
		
		tempDirectory.create();
		temporaryDirectory = tempDirectory.newFolder("temp");
		uuidGenerator = new UuidGenerator();
		saxParserFactory = SAXParserFactory.newInstance();
		titleMetadata = getTitleMetadata();
		titleManifestOutputStream = new FileOutputStream(tempDirectory.newFile("title.xml"));
		saxParserFactory.setNamespaceAware(true);
	    saxParser = saxParserFactory.newSAXParser();
	    xmlReader = saxParser.getXMLReader();
	    mockFileUtilsFacade = EasyMock.createMock(FileUtilsFacade.class);
	    mockPlaceholderDocumentService = EasyMock.createMock(PlaceholderDocumentService.class);
	    titleMetadataService = new  TitleMetadataServiceImpl();
	    Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
	    props.setProperty("omit-xml-declaration", "yes");
	    
	    serializer = SerializerFactory.getSerializer(props);
	    resultStream = new ByteArrayOutputStream(1024);
	    serializer.setOutputStream(resultStream);
	    
	   // URL pathToClass = this.getClass().getResource("yarr_pirates.csv");
	    //altIdFile = new File(pathToClass.toURI());
		
	    tocXml = new ByteArrayInputStream("<EBook><EBookToc><Name>BLARGH</Name><Guid>TOC_GUID</Guid><DocumentGuid>DOC_GUID</DocumentGuid></EBookToc></EBook>".getBytes());
	    Map<String,String> altIdMap = new HashMap<String,String>();
	    docImageMap = new HashMap<String, List<String>>();
	    splitTocManifestFilterTest = new SplitTocManifestFilter(titleMetadata, new HashMap<String, String>(), uuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, docImageMap);
	    splitTocManifestFilterTest.setParent(xmlReader);
		splitTocManifestFilterTest.setContentHandler(serializer.asContentHandler());
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testStartDocument() throws Exception {
		splitTocManifestFilterTest.startDocument();
		splitTocManifestFilterTest.endDocument();
		Assert.assertEquals("<title/>", resultStreamToString(resultStream));
	}
	
	@Test
	public void testEndDocumentWithoutParsingToc() throws Exception {
		splitTocManifestFilterTest.endDocument();		
		Assert.assertEquals(EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testEndDocumentWithParsingToc() throws Exception {
		splitTocManifestFilterTest.parse(new InputSource(tocXml));
		String expected ="<title><toc><titlebreak/>"
				+ "<entry s=\"yarr/pirates#FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry>"
				+"<entry s=\"yarr/pirates#Copyright/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text>"
				+ "<entry s=\"yarr/pirates#Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry>"
				+ "<entry s=\"yarr/pirates#ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry>"
				+ "<entry s=\"yarr/pirates#WestlawNext/WestlawNextAnchor\"><text>WestlawNext</text></entry></entry><entry s=\"yarr/pirates#DOC_GUID/TOC_GUID\"><text>BLARGH</text></entry></toc></title>";
		Assert.assertEquals(expected, resultStreamToString(resultStream));
	}
	
	@Test
	public void testTitleManifestFilterHappyPath() throws Exception {
		titleMetadata.setTitleId("uscl/an/book_splittitletest");
		InputStream immigrationProceduresHandbook = SplitTocManifestFilterTest.class.getResourceAsStream("SPLIT_TOC.xml");
		splitTocManifestFilterTest.parse(new InputSource(immigrationProceduresHandbook));
		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expected = new InputSource(SplitTocManifestFilterTest.class.getResourceAsStream("SPLIT_TITLE.xml"));
		
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		List<Difference> differences = diff.getAllDifferences();
		Assert.assertTrue(differences.size() == 0); 
	}
	
	@Test
	public void testFamilyGuidReplacementHappyPath() throws Exception {
		Map<String, String> familyGuidMap = new HashMap<String, String>();
		familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
		familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
		familyGuidMap.put("DOC_GUID3", "FAM_GUID3");
		String input = "<EBook>" +
				"<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>" +
				"</EBook>";
		
		String expectedToc = "<toc><titlebreak/><entry s=\"yarr/pirates#FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry><entry s=\"yarr/pirates#Copyright/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text><entry s=\"yarr/pirates#Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry><entry s=\"yarr/pirates#AdditionalFrontMatter1/AdditionalFrontMatter1Anchor\"><text>Pirates Toc Page</text></entry><entry s=\"yarr/pirates#ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry><entry s=\"yarr/pirates#WestlawNext/WestlawNextAnchor\"><text>WestlawNext</text></entry></entry><entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#FAM_GUID3/TOC_GUID3\"><text>3</text></entry></toc>";

		String expected = "<title>"+ expectedToc + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		
		FrontMatterPage fmPage = new FrontMatterPage();
		fmPage.setId((long) 1);
		fmPage.setPageTocLabel("Pirates Toc Page");
		List <FrontMatterPage> frontMatterPages = new ArrayList<FrontMatterPage>();
		frontMatterPages.add(fmPage);
		titleMetadata.setFrontMatterPages(frontMatterPages);
		
		SplitTocManifestFilter filter = new SplitTocManifestFilter(titleMetadata, familyGuidMap, uuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, docImageMap);
	    
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
		//System.out.println(expected+"---------"+resultStreamToString(resultStream));
		InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));
		
		assertXMLEqual(expectedInputSource, resultInputSource);
	}
	
	@Test
	public void testDuplicateDocGuidHappyPath() throws Exception {
		Map<String, String> familyGuidMap = new HashMap<String, String>();
		familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
		familyGuidMap.put("DOC_GUID3", "FAM_GUID3");
		String input = "<EBook>" +
				"<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>" +
				"</EBook>";
		
		
		
		String expectedToc = EXPECTED__SPLIT_TOC + "<entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#GENERATED_DOC_GUID/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#FAM_GUID3/TOC_GUID3\"><text>3</text></entry></toc>";
		String expected = "<title>" + expectedToc + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
		EasyMock.replay(mockUuidGenerator);
		
		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
	    SplitTocManifestFilter filter = new SplitTocManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, docImageMap);
	    
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
		
		
		InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));
		
		assertXMLEqual(expectedInputSource, resultInputSource);
	}
	
	@Test
	public void testDuplicateFamilyGuidHappyPath() throws Exception {
		Map<String, String> familyGuidMap = new HashMap<String, String>();
		familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
		familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
		familyGuidMap.put("DOC_GUID3", "FAM_GUID1");
		String input = "<EBook>" +
				"<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>" +
				"</EBook>";
		
		String expectedToc = EXPECTED__SPLIT_TOC + "<entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry></toc>";
		String expected = "<title>" + expectedToc + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
		EasyMock.replay(mockUuidGenerator);
		SplitTocManifestFilter filter = new SplitTocManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, docImageMap);
	    
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
		
		InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));
		
		assertXMLEqual(expectedInputSource, resultInputSource);
	}
	
	@Test
	public void testDuplicateDocGuidAndFamilyGuidHappyPath() throws Exception {
		Map<String, String> familyGuidMap = new HashMap<String, String>();
		familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
		familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
		familyGuidMap.put("DOC_GUID3", "FAM_GUID1");
		String input = "<EBook>" +
				"<EBookToc><Name>1</Name><Guid>TOC_GUID1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>2</Name><Guid>TOC_GUID2</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>3</Name><Guid>TOC_GUID3</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>" +
				"<EBookToc><Name>4</Name><Guid>TOC_GUID4</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>" +
				"</EBook>";
		
		String expectedToc = EXPECTED__SPLIT_TOC + "<entry s=\"yarr/pirates#FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"yarr/pirates#FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"yarr/pirates#GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry><entry s=\"yarr/pirates#GENERATED_DOC_GUID/TOC_GUID4\"><text>4</text></entry></toc>";
		String expected = "<title>" + expectedToc +  EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		

		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
	    mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
		EasyMock.replay(mockUuidGenerator);
		SplitTocManifestFilter filter = new SplitTocManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, docImageMap);
	    
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
		
		InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));
		assertXMLEqual(expectedInputSource, resultInputSource);
	}
	
	/**
	 * If this test does not pass something has gone seriously wrong with the filter; possibly related to the way we cascade identifiers.
	 */
	@Test
	public void testCascadeAcrossSiblingsWorksProperlyCaFedRules2012() throws Exception {
		Map<String, String> familyGuidMap = new HashMap<String, String>();
		familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
		familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
		familyGuidMap.put("DOC_GUID3", "FAM_GUID1");
		
		InputStream caFedRules2012 = SplitTocManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012.xml");

		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_1");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_2");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_3");
		EasyMock.replay(mockUuidGenerator);
		SplitTocManifestFilter filter = new SplitTocManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, docImageMap);
	    
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		
		filter.parse(new InputSource(caFedRules2012));
		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expected = new InputSource(SplitTocManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_EXPECTED_SPLIT_MANIFEST.xml"));
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		List<Difference> differences = diff.getAllDifferences();
		Assert.assertTrue(differences.size() == 0); 
	}
	
	@Test
	public void testCaFedRules2012WithoutDeletedNodes() throws Exception {
		InputStream caFedRules2012 = SplitTocManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_WITHOUT_DELETED_NODES.xml");
		splitTocManifestFilterTest.parse(new InputSource(caFedRules2012));
		//System.out.println(resultStreamToString(resultStream));
		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expected = new InputSource(SplitTocManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_WITHOUT_DELETED_NODES_EXPECTED.xml"));
		
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		List<Difference> differences = diff.getAllDifferences();
		Assert.assertTrue(differences.size() == 0); 
	}
	
	@Test
	public void testWriteTocNodeHappyPath() throws Exception {
		TableOfContents tableOfContents = new TableOfContents();
		tableOfContents.addChild(new TocEntry("TOC1", "DOC1", "FOO", 1));
		tableOfContents.addChild(new TocEntry("TOC2", "DOC2", "BAR", 1));
		TocNode basil = new TocEntry("TOC3", "DOC3", "BAZ", 1);
		basil.addChild(new TocEntry("TOC3.1", "DOC3.1", "BASIL", 2));
		tableOfContents.addChild(basil);
		splitTocManifestFilterTest.setTableOfContents(tableOfContents);
		splitTocManifestFilterTest.endDocument(); //indirectly tests writeTableOfContents, which is called as part of invoking endDocument().
		String expected = "<toc><titlebreak/><entry s=\"DOC1/TOC1\"><text>FOO</text></entry><entry s=\"DOC2/TOC2\"><text>BAR</text></entry><entry s=\"DOC3/TOC3\"><text>BAZ</text><entry s=\"DOC3.1/TOC3.1\"><text>BASIL</text></entry></entry></toc></title>";
		Assert.assertEquals(expected, resultStreamToString(resultStream));
	}
	
	
	private String resultStreamToString(ByteArrayOutputStream resultStream) throws Exception {
		return IOUtils.toString(resultStream.toByteArray(), "UTF-8");
	}
}

