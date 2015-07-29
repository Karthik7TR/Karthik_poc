/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
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

/**
 * Test suite for the title manifest filter.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 *
 */
public class TitleManifestFilterTest extends TitleMetadataTestBase {
	private static final Logger LOG = Logger.getLogger(TitleManifestFilterTest.class);
	UuidGenerator uuidGenerator;
	SAXParserFactory saxParserFactory;
	TitleManifestFilter titleManifestFilter;
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
    	    
    
	private static final String EXPECTED_ARTWORK = "<artwork src=\"swashbuckling.gif\" type=\"cover\"/>";
	private static final String EXPECTED_ASSETS = "<assets><asset id=\"123\" src=\"BlackPearl.png\"/><asset id=\"456\" src=\"PiratesCove.png\"/><asset id=\"789\" src=\"Tortuga.png\"/></assets>";
	private static final String EXPECTED_AUTHORS = "<authors><author>Captain Jack Sparrow</author><author>Davey Jones</author></authors>";
	private static final String EXPECTED_KEYWORDS = "<keywords><keyword type=\"publisher\">High Seas Trading Company</keyword><keyword type=\"jurisdiction\">International Waters</keyword></keywords>";
	private static final String EXPECTED_COPYRIGHT = "<copyright>The High Seas Trading Company.</copyright>";
	private static final String EXPECTED_DISPLAYNAME = "<name>YARR - The Comprehensive Guide to &amp;&lt;&gt; Plundering the Seven Seas.</name>";
	private static final String EXPECTED_FEATURES = "<features><feature name=\"AutoUpdate\"/><feature name=\"SearchIndex\"/><feature name=\"OnePassSSO\" value=\"www.westlaw.com\"/></features>";
	private static final String EXPECTED_MATERIAL_ID = "<material>Plunder2</material>";
	private static final String EXPECTED_START_MANIFEST_PREFIX = "<title apiversion=\"v1\" titleversion=\"v1\" id=\"yarr/pirates\" lastupdated=\"";
	private static final String EXPECTED_START_MANIFEST_SUFFIX = "\" language=\"eng\" status=\"Review\" onlineexpiration=\"";
	private static final String EXPECTED_START_MANIFEST_SUFFIX_SINGLETON = "\" language=\"eng\" status=\"Review\" onlineexpiration=\"";
	private static final String EXPECTED_TOC = "<toc><entry s=\"FrontMatterTitle/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text><entry s=\"FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry><entry s=\"Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry><entry s=\"ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry><entry s=\"WestlawNext/WestlawNextAnchor\"><text>WestlawNext</text></entry></entry>";
	private static final String CASCADED_TOC = "<entry s=\"DOC_GUID/TOC_GUID\"><text>BLARGH</text></entry></toc>";
	private static final String EXPECTED_DOCS_TOC_ENTRIES = "<docs><doc id=\"FrontMatterTitle\" src=\"FrontMatterTitle.html\"/><doc id=\"Copyright\" src=\"Copyright.html\"/><doc id=\"ResearchAssistance\" src=\"ResearchAssistance.html\"/><doc id=\"WestlawNext\" src=\"WestlawNext.html\"/>";
	private static final String EXPECTED_ALT_ID_ENTRIES = "<docs><doc id=\"FrontMatterTitle\" altid=\"9 doc_0001\" src=\"FrontMatterTitle.html\"/><doc id=\"Copyright\" altid=\"3 doc_0002\" src=\"Copyright.html\"/><doc id=\"ResearchAssistance\" altid=\"3 doc_0004\" src=\"ResearchAssistance.html\"/><doc id=\"WestlawNext\" altid=\"1 doc_0005\" src=\"WestlawNext.html\"/>";
	private static final String EXPECTED_DOCS = EXPECTED_DOCS_TOC_ENTRIES + "<doc id=\"DOC_GUID\" src=\"DOC_GUID.html\"/></docs>";
	private static final String EXPECTED_END_MANIFEST = "</title>";
	File altIdDir;
	
	String lastupdated = new SimpleDateFormat("yyyyMMdd").format(new Date());
	String onlineexpiration = "29991231";
	

	@Rule
	public static TemporaryFolder tempDirectory = new TemporaryFolder();
	File temporaryDirectory;
	
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
	    
	    URL pathToClass = this.getClass().getResource("yarr_pirates.csv");
	    altIdFile = new File(pathToClass.toURI());
		
	    tocXml = new ByteArrayInputStream("<EBook><EBookToc><Name>BLARGH</Name><Guid>TOC_GUID</Guid><DocumentGuid>DOC_GUID</DocumentGuid></EBookToc></EBook>".getBytes());
	    Map<String,String> altIdMap = new HashMap<String,String>();
	    titleManifestFilter = new TitleManifestFilter(titleMetadata, new HashMap<String, String>(), uuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
	    titleManifestFilter.setParent(xmlReader);
		titleManifestFilter.setContentHandler(serializer.asContentHandler());
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testWriteAssets() throws Exception {
		titleManifestFilter.writeAssets();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_ASSETS + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteAuthors() throws Exception {
		titleManifestFilter.writeAuthors();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_AUTHORS +  EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteKeywords() throws Exception {
		titleManifestFilter.writeKeywords();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_KEYWORDS + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteCopyright() throws Exception {
		titleManifestFilter.writeCopyright();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_COPYRIGHT + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testStartManifest() throws Exception {
		titleManifestFilter.startManifest();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX_SINGLETON + onlineexpiration + "\"/>", resultStreamToString(resultStream));
	}

	@Test
	public void testWriteDisplayName() throws Exception {
		titleManifestFilter.writeDisplayName();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_DISPLAYNAME +  EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteFeatures() throws Exception {
		titleManifestFilter.writeFeatures();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_FEATURES + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteMaterialId() throws Exception {
		titleManifestFilter.writeMaterialId();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_MATERIAL_ID + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteCoverArt() throws Exception {
		titleManifestFilter.writeCoverArt();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_ARTWORK + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testStartDocument() throws Exception {
		titleManifestFilter.startDocument();
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testEndDocumentWithoutParsingToc() throws Exception {
		titleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testEndDocumentWithParsingToc() throws Exception {
		titleManifestFilter.parse(new InputSource(tocXml));
		Assert.assertEquals(EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + EXPECTED_TOC + CASCADED_TOC + EXPECTED_DOCS + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testTitleManifestFilterHappyPath() throws Exception {
		InputStream immigrationProceduresHandbook = TitleManifestFilterTest.class.getResourceAsStream("IMPH_L1_EXTENSIONS.xml");
		titleManifestFilter.parse(new InputSource(immigrationProceduresHandbook));
		System.out.println(resultStreamToString(resultStream));
		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
//		IOUtils.copy(new ByteArrayInputStream(resultStream.toByteArray()), new FileOutputStream(new File("C:\\Users\\u0081674\\TitleManifestFilterTestOutput.xml")));
		InputSource expected = new InputSource(TitleManifestFilterTest.class.getResourceAsStream("IMPH_L1_EXTENSIONS_EXPECTED_MANIFEST.xml"));
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		System.out.println(diff.getAllDifferences());
		List<Difference> differences = diff.getAllDifferences();
		Assert.assertTrue(differences.size() == 1); //the only thing that should be different between the control file and this run is the last updated date.
		Difference difference = differences.iterator().next();
		String actualDifferenceLocation = difference.getTestNodeDetail().getXpathLocation();
		String expectedDifferenceLocation = "/title[1]/@lastupdated";
		Assert.assertEquals(expectedDifferenceLocation, actualDifferenceLocation);
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
		
		String expectedDocs = "<docs><doc id=\"FrontMatterTitle\" src=\"FrontMatterTitle.html\"/><doc id=\"Copyright\" src=\"Copyright.html\"/><doc id=\"AdditionalFrontMatter1\" src=\"AdditionalFrontMatter1.html\"/><doc id=\"ResearchAssistance\" src=\"ResearchAssistance.html\"/><doc id=\"WestlawNext\" src=\"WestlawNext.html\"/><doc id=\"FAM_GUID1\" src=\"DOC_GUID1.html\"/><doc id=\"FAM_GUID2\" src=\"DOC_GUID2.html\"/><doc id=\"FAM_GUID3\" src=\"DOC_GUID3.html\"/></docs>";
		String expectedToc = "<toc><entry s=\"FrontMatterTitle/PublishingInformationAnchor\"><text>PUBLISHING INFORMATION</text><entry s=\"FrontMatterTitle/FrontMatterTitleAnchor\"><text>Title Page</text></entry><entry s=\"Copyright/CopyrightAnchor\"><text>Copyright Page</text></entry><entry s=\"AdditionalFrontMatter1/AdditionalFrontMatter1Anchor\"><text>Pirates Toc Page</text></entry><entry s=\"ResearchAssistance/ResearchAssistanceAnchor\"><text>Additional Information or Research Assistance</text></entry><entry s=\"WestlawNext/WestlawNextAnchor\"><text>WestlawNext</text></entry></entry><entry s=\"FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"FAM_GUID3/TOC_GUID3\"><text>3</text></entry></toc>";

		String expected = EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + expectedToc + expectedDocs + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		
		FrontMatterPage fmPage = new FrontMatterPage();
		fmPage.setId((long) 1);
		fmPage.setPageTocLabel("Pirates Toc Page");
		List <FrontMatterPage> frontMatterPages = new ArrayList<FrontMatterPage>();
		frontMatterPages.add(fmPage);
		titleMetadata.setFrontMatterPages(frontMatterPages);
		Map<String,String> altIdMap = new HashMap<String,String>();
		TitleManifestFilter filter = new TitleManifestFilter(titleMetadata, familyGuidMap, uuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
		System.out.println(resultStreamToString(resultStream));
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
		
		String expectedDocs = EXPECTED_DOCS_TOC_ENTRIES +"<doc id=\"FAM_GUID1\" src=\"DOC_GUID1.html\"/><doc id=\"GENERATED_DOC_GUID\" src=\"GENERATED_DOC_GUID.html\"/><doc id=\"FAM_GUID3\" src=\"DOC_GUID3.html\"/></docs>";
		String expectedToc = EXPECTED_TOC + "<entry s=\"FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"GENERATED_DOC_GUID/TOC_GUID2\"><text>2</text></entry><entry s=\"FAM_GUID3/TOC_GUID3\"><text>3</text></entry></toc>";
		String expected = EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + expectedToc + expectedDocs + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
		EasyMock.replay(mockUuidGenerator);
		
		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
	    Map<String,String> altIdMap = new HashMap<String,String>();
		TitleManifestFilter filter = new TitleManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
				
		System.out.println(resultStreamToString(resultStream));
		
		
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
		
		String expectedDocs = EXPECTED_DOCS_TOC_ENTRIES +"<doc id=\"FAM_GUID1\" src=\"DOC_GUID1.html\"/><doc id=\"FAM_GUID2\" src=\"DOC_GUID2.html\"/><doc id=\"GENERATED_FAMILY_GUID\" src=\"GENERATED_FAMILY_GUID.html\"/></docs>";
		String expectedToc = EXPECTED_TOC + "<entry s=\"FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry></toc>";
		String expected = EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + expectedToc + expectedDocs + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
		EasyMock.replay(mockUuidGenerator);
		Map<String,String> altIdMap = new HashMap<String,String>();
		TitleManifestFilter filter = new TitleManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
				
		System.out.println(resultStreamToString(resultStream));
		
		
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
		
		String expectedDocs = EXPECTED_DOCS_TOC_ENTRIES +"<doc id=\"FAM_GUID1\" src=\"DOC_GUID1.html\"/><doc id=\"FAM_GUID2\" src=\"DOC_GUID2.html\"/><doc id=\"GENERATED_FAMILY_GUID\" src=\"GENERATED_FAMILY_GUID.html\"/><doc id=\"GENERATED_DOC_GUID\" src=\"GENERATED_DOC_GUID.html\"/></docs>";
		String expectedToc = EXPECTED_TOC + "<entry s=\"FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry><entry s=\"GENERATED_DOC_GUID/TOC_GUID4\"><text>4</text></entry></toc>";
		String expected = EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + expectedToc + expectedDocs + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		

		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
	    mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
		EasyMock.replay(mockUuidGenerator);
		Map<String,String> altIdMap = new HashMap<String,String>();
		TitleManifestFilter filter = new TitleManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
				
		System.out.println(resultStreamToString(resultStream));
		
		
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
		
		InputStream caFedRules2012 = TitleManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012.xml");

		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_1");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_2");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_PLACEHOLDER_DOCUMENT_UUID_3");
		EasyMock.replay(mockUuidGenerator);
		Map<String,String> altIdMap = new HashMap<String,String>();
		TitleManifestFilter filter = new TitleManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		
		filter.parse(new InputSource(caFedRules2012));
		System.out.println(resultStreamToString(resultStream));
		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expected = new InputSource(TitleManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_EXPECTED_MANIFEST.xml"));
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		System.out.println(diff.getAllDifferences());
		List<Difference> differences = diff.getAllDifferences();
		Assert.assertTrue(differences.size() == 1); //the only thing that should be different between the control file and this run is the last updated date.
		Difference difference = differences.iterator().next();
		String actualDifferenceLocation = difference.getTestNodeDetail().getXpathLocation();
		String expectedDifferenceLocation = "/title[1]/@lastupdated";
		Assert.assertEquals(expectedDifferenceLocation, actualDifferenceLocation);
	}
	
	@Test
	public void testCaFedRules2012WithoutDeletedNodes() throws Exception {
		InputStream caFedRules2012 = TitleManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_WITHOUT_DELETED_NODES.xml");
		titleManifestFilter.parse(new InputSource(caFedRules2012));
		System.out.println(resultStreamToString(resultStream));
		InputSource result = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expected = new InputSource(TitleManifestFilterTest.class.getResourceAsStream("CA_FED_RULES_2012_EXPECTED_MANIFEST_WITHOUT_DELETED_NODES.xml"));
		DetailedDiff diff = new DetailedDiff(compareXML(expected, result));
		System.out.println(diff.getAllDifferences());
		List<Difference> differences = diff.getAllDifferences();
		Assert.assertTrue(differences.size() == 1); //the only thing that should be different between the control file and this run is the last updated date.
		Difference difference = differences.iterator().next();
		String actualDifferenceLocation = difference.getTestNodeDetail().getXpathLocation();
		String expectedDifferenceLocation = "/title[1]/@lastupdated";
		Assert.assertEquals(expectedDifferenceLocation, actualDifferenceLocation);
	}
	
	@Test
	public void testWriteTocNodeHappyPath() throws Exception {
		TableOfContents tableOfContents = new TableOfContents();
		tableOfContents.addChild(new TocEntry("TOC1", "DOC1", "FOO", 1));
		tableOfContents.addChild(new TocEntry("TOC2", "DOC2", "BAR", 1));
		TocNode basil = new TocEntry("TOC3", "DOC3", "BAZ", 1);
		basil.addChild(new TocEntry("TOC3.1", "DOC3.1", "BASIL", 2));
		tableOfContents.addChild(basil);
		titleManifestFilter.setTableOfContents(tableOfContents);
		titleManifestFilter.endDocument(); //indirectly tests writeTableOfContents, which is called as part of invoking endDocument().
		System.out.println(resultStreamToString(resultStream));
		String expected = "<toc><entry s=\"DOC1/TOC1\"><text>FOO</text></entry><entry s=\"DOC2/TOC2\"><text>BAR</text></entry><entry s=\"DOC3/TOC3\"><text>BAZ</text><entry s=\"DOC3.1/TOC3.1\"><text>BASIL</text></entry></entry></toc></title>";
		Assert.assertEquals(expected, resultStreamToString(resultStream));
	}
	
	
	private String resultStreamToString(ByteArrayOutputStream resultStream) throws Exception {
		return IOUtils.toString(resultStream.toByteArray(), "UTF-8");
	}
	
	@Test
	public void testAlternativeId() throws Exception {
		titleMetadata = getTitleMetadataWithPilotBook();
		URL pathToClass = this.getClass().getResource("yarr_pirates.csv");
	    altIdFile = new File(pathToClass.toURI());
	    System.out.println(altIdFile.getAbsolutePath()+" : "+altIdFile.getParent());
	    Map<String,String> altIdMap = titleMetadataService.getAltIdMap("yarr_pirates",altIdFile.getParent());
		//Map<String,String> altIdMap = new HashMap<String,String>();
		titleManifestFilter = new TitleManifestFilter(titleMetadata, new HashMap<String, String>(), uuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
		titleManifestFilter.setParent(xmlReader);
		titleManifestFilter.setContentHandler(serializer.asContentHandler());
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
		
		String expectedDocs = EXPECTED_ALT_ID_ENTRIES +"<doc id=\"FAM_GUID1\" altid=\"test_FAM_GUID1\" src=\"DOC_GUID1.html\"/><doc id=\"FAM_GUID2\" altid=\"test_FAM_GUID2\" src=\"DOC_GUID2.html\"/><doc id=\"GENERATED_FAMILY_GUID\" altid=\"test_FAM_GUID3\" src=\"GENERATED_FAMILY_GUID.html\"/><doc id=\"GENERATED_DOC_GUID\" altid=\"test_GENERATED_DOC_GUID\" src=\"GENERATED_DOC_GUID.html\"/></docs>";
		String expectedToc = EXPECTED_TOC + "<entry s=\"FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry><entry s=\"GENERATED_DOC_GUID/TOC_GUID4\"><text>4</text></entry></toc>";
		String expected = EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + expectedToc + expectedDocs + EXPECTED_END_MANIFEST;
		
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		

		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
	    mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
		EasyMock.replay(mockUuidGenerator);
		TitleManifestFilter filter = new TitleManifestFilter(titleMetadata, familyGuidMap, mockUuidGenerator, temporaryDirectory, mockFileUtilsFacade, mockPlaceholderDocumentService, altIdMap);
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
				
		System.out.println(resultStreamToString(resultStream));
		
		
		InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));
		assertXMLEqual(expectedInputSource, resultInputSource);
	}
	
}
