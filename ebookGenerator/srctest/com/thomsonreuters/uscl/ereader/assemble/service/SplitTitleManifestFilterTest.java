/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
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
 import org.apache.tools.ant.util.FileUtils;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;

public class SplitTitleManifestFilterTest extends TitleMetadataTestBase {
	UuidGenerator uuidGenerator;
	SAXParserFactory saxParserFactory;
	SplitTitleManifestFilter splitTitleManifestFilter;
	TitleMetadata titleMetadata;
	OutputStream titleManifestOutputStream;
	InputStream tocXml;
	InputStream splitTocTitleXml;
	Serializer serializer;
	SAXParser saxParser;
	XMLReader xmlReader;
	ByteArrayOutputStream resultStream;
	FileUtilsFacade mockFileUtilsFacade;
    PlaceholderDocumentService mockPlaceholderDocumentService;
    TitleMetadataServiceImpl titleMetadataService;
    File altIdFile;
    List<Doc> docList;
    	    
    
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
	    docList = new ArrayList<>();
	    splitTitleManifestFilter = new SplitTitleManifestFilter(titleMetadata,docList,altIdMap);
	    splitTitleManifestFilter.setParent(xmlReader);
		splitTitleManifestFilter.setContentHandler(serializer.asContentHandler());
		
		String s = "<title>"+EXPECTED_TOC + CASCADED_TOC + EXPECTED_DOCS + EXPECTED_END_MANIFEST;
		splitTocTitleXml= new ByteArrayInputStream(s.getBytes());
	    
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.delete(temporaryDirectory);
	}
	
	@Test
	public void testWriteAssets() throws Exception {
		splitTitleManifestFilter.writeAssets();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_ASSETS + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteAuthors() throws Exception {
		splitTitleManifestFilter.writeAuthors();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_AUTHORS + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteKeywords() throws Exception {
		splitTitleManifestFilter.writeKeywords();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_KEYWORDS + EXPECTED_ISBN +EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteCopyright() throws Exception {
		splitTitleManifestFilter.writeCopyright();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_COPYRIGHT + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testStartManifest() throws Exception {
		splitTitleManifestFilter.startManifest();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX_SINGLETON + onlineexpiration + "\">" + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}

	@Test
	public void testWriteDisplayName() throws Exception {
		splitTitleManifestFilter.writeDisplayName();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_DISPLAYNAME + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteFeatures() throws Exception {
		splitTitleManifestFilter.writeFeatures();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_FEATURES + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteMaterialId() throws Exception {
		splitTitleManifestFilter.writeMaterialId();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_MATERIAL_ID + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testWriteCoverArt() throws Exception {
		splitTitleManifestFilter.writeCoverArt();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_ARTWORK + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testStartDocument() throws Exception {
		splitTitleManifestFilter.startDocument();
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testEndDocumentWithoutParsingToc() throws Exception {
		splitTitleManifestFilter.endDocument();
		Assert.assertEquals(EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	@Test
	public void testEndDocumentWithParsingToc() throws Exception {
		splitTitleManifestFilter.parse(new InputSource(splitTocTitleXml));
		//System.out.println(resultStreamToString(resultStream));
		Assert.assertEquals(EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + EXPECTED_TOC + CASCADED_TOC + EXPECTED_DOCS + EXPECTED_ISBN + EXPECTED_END_MANIFEST, resultStreamToString(resultStream));
	}
	
	
	private String resultStreamToString(ByteArrayOutputStream resultStream) throws Exception {
		return IOUtils.toString(resultStream.toByteArray(), "UTF-8");
	}
	
	
	@Test
	public void testAlternativeId() throws Exception {
		titleMetadata = getTitleMetadataWithPilotBook();
		URL pathToClass = this.getClass().getResource("yarr_pirates.csv");
	    altIdFile = new File(pathToClass.toURI());
	    Map<String,String> altIdMap = titleMetadataService.getAltIdMap("yarr_pirates",altIdFile.getParent());
		splitTitleManifestFilter = new SplitTitleManifestFilter(titleMetadata,docList,altIdMap);
		splitTitleManifestFilter.setParent(xmlReader);
		splitTitleManifestFilter.setContentHandler(serializer.asContentHandler());
		Map<String, String> familyGuidMap = new HashMap<String, String>();
		familyGuidMap.put("DOC_GUID1", "FAM_GUID1");
		familyGuidMap.put("DOC_GUID2", "FAM_GUID2");
		familyGuidMap.put("DOC_GUID3", "FAM_GUID1");
		
		
		String expectedDocs = EXPECTED_ALT_ID_ENTRIES +"<doc id=\"FAM_GUID1\" altid=\"test_FAM_GUID1\" src=\"DOC_GUID1.html\"/><doc id=\"FAM_GUID2\" altid=\"test_FAM_GUID2\" src=\"DOC_GUID2.html\"/><doc id=\"GENERATED_FAMILY_GUID\" altid=\"test_FAM_GUID3\" src=\"GENERATED_FAMILY_GUID.html\"/><doc id=\"GENERATED_DOC_GUID\" altid=\"test_GENERATED_DOC_GUID\" src=\"GENERATED_DOC_GUID.html\"/></docs>";
		String expectedToc = EXPECTED_TOC + "<entry s=\"FAM_GUID1/TOC_GUID1\"><text>1</text></entry><entry s=\"FAM_GUID2/TOC_GUID2\"><text>2</text></entry><entry s=\"GENERATED_FAMILY_GUID/TOC_GUID3\"><text>3</text></entry><entry s=\"GENERATED_DOC_GUID/TOC_GUID4\"><text>4</text></entry></toc>";
		String expected = EXPECTED_START_MANIFEST_PREFIX + lastupdated + EXPECTED_START_MANIFEST_SUFFIX + onlineexpiration + "\">" +
				EXPECTED_FEATURES + EXPECTED_MATERIAL_ID + EXPECTED_ARTWORK + EXPECTED_ASSETS +
				EXPECTED_DISPLAYNAME + EXPECTED_AUTHORS + EXPECTED_KEYWORDS + EXPECTED_COPYRIGHT + expectedToc + expectedDocs + EXPECTED_ISBN + EXPECTED_END_MANIFEST;
		
		String input = "<title>"+expectedToc + expectedDocs+ EXPECTED_END_MANIFEST;
		InputStream inputXml = new ByteArrayInputStream(input.getBytes());
		

		mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID3.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_FAMILY_GUID.html"));
	    mockFileUtilsFacade.copyFile(new File(temporaryDirectory.getAbsolutePath() + "\\DOC_GUID1.html"), new File(temporaryDirectory.getAbsolutePath() + "\\GENERATED_DOC_GUID.html"));
	    EasyMock.replay(mockFileUtilsFacade);
		
		UuidGenerator mockUuidGenerator = EasyMock.createMock(UuidGenerator.class);
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_FAMILY_GUID");
		EasyMock.expect(mockUuidGenerator.generateUuid()).andReturn("GENERATED_DOC_GUID");
		EasyMock.replay(mockUuidGenerator);
		SplitTitleManifestFilter filter = new SplitTitleManifestFilter(titleMetadata,docList,altIdMap);
		filter.setParent(xmlReader);
		filter.setContentHandler(serializer.asContentHandler());
		filter.parse(new InputSource(inputXml));
			
		
		InputSource resultInputSource = new InputSource(new ByteArrayInputStream(resultStream.toByteArray()));
		InputSource expectedInputSource = new InputSource(new ByteArrayInputStream(expected.getBytes()));
		
		
		assertXMLEqual(expectedInputSource, resultInputSource);
	}
	
}
