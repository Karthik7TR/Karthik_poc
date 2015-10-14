package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;

public class AutoSplitGuiIdsHandlerTest {

	private AutoSplitNodesHandler splitBookFilter;
	private Serializer serializer;
	List<String> splitTocGuidList;
	private static final String FINE_NAME = "toc.xml";
	InputStream tocXml;
	File tocFile;

	@Before
	public void setUp() throws Exception {

		URL url = AutoSplitGuiIdsHandlerTest.class.getResource(FINE_NAME);
		tocFile = new File(url.getPath());

	}

	@After
	public void tearDown() throws Exception {
		serializer = null;
		splitBookFilter = null;
	}

	@Test
	public void testMargin() throws Exception {
		splitBookFilter = new AutoSplitNodesHandler(7500, 10);
		Assert.assertEquals(new Integer(750), splitBookFilter.getMargin(7500));
	}
	
	@Test
	public void testMarginWithuneven() throws Exception {
		splitBookFilter = new AutoSplitNodesHandler(7523, 10);
		Assert.assertEquals(new Integer(752), splitBookFilter.getMargin(7523));
	}

	@Test
	public void testWithFile() throws Exception {
		tocXml = new FileInputStream(tocFile);
		Map<String, String> splitTocGuidList = testHelper(tocXml, 25, 10);
		Assert.assertEquals(splitTocGuidList.size(), 4);
	}

	@Test
	public void test() {
		BookDefinition book = new BookDefinition();
		List<SplitDocument> expectTedGuidList = new ArrayList<SplitDocument>();

		SplitDocument s = new SplitDocument();
		s.setTocGuid("TABLEOFCONTENTS33CHARACTERSLONG_4");
		s.setBookDefinition(book);
		expectTedGuidList.add(s);

		book.setSplitDocuments(new HashSet<SplitDocument>(expectTedGuidList));

		System.out.println(book.getSplitDocuments().toString());

	}

	@Test
	public void testWithString() throws Exception {
		String xmlTestStr = "<EBook>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
				+ "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
				+ "</EBook>";

		List<String> expectTedGuidList = new ArrayList<String>();
		expectTedGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_5");

		ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
		Map<String, String> splitTocGuidList = testHelper(input, 2, 10);
		
		Assert.assertEquals(splitTocGuidList.size(), expectTedGuidList.size());
		for (Map.Entry<String, String> entry : splitTocGuidList.entrySet()) {
			String uuid = entry.getKey();
			Assert.assertEquals(expectTedGuidList.get(0).toString(),uuid);
		}

		
	}

	public Map<String, String> testHelper(InputStream inputXML, int splitSize, int percent) throws SAXException {
		Map<String, String> splitTocGuidList = new HashMap<String, String>();
		ByteArrayOutputStream output = null;
		try {

			output = new ByteArrayOutputStream();

			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();

			splitBookFilter = new AutoSplitNodesHandler(splitSize, percent);

			splitBookFilter.setParent(saxParser.getXMLReader());

			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
			props.setProperty("omit-xml-declaration", "yes");
			serializer = SerializerFactory.getSerializer(props);

			splitBookFilter.setDeterminedPartSize(splitSize);
			splitBookFilter.setContentHandler(serializer.asContentHandler());

			Reader reader = new InputStreamReader(inputXML, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			splitBookFilter.parse(is);
			splitTocGuidList = splitBookFilter.getSplitTocTextMap();

			
			/*for (Map.Entry<String, String> entry : splitTocGuidList.entrySet()) {
				String uuid = entry.getKey();
				System.out.println(entry.getValue() + "----------------Toc UUID-----" + uuid);
			}*/

		} catch (Exception e) {
			fail("Encountered exception during test: " + e.getMessage());
		} finally {
			try {
				if (inputXML != null) {
					inputXML.close();
				}
				if (output != null) {
					output.close();
				}
			} catch (Exception e) {
				fail("Couldn't clean up resources: " + e.getMessage());
			}
		}

		return splitTocGuidList;
	}

	

}
