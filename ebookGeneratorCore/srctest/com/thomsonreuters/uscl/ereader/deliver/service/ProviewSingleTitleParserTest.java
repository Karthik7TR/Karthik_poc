package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;

public class ProviewSingleTitleParserTest {
	private ProviewSingleTitleParser parser;
	private XMLReader reader;

	@Before
	public void setUp() throws Exception {
		parser = new ProviewSingleTitleParser();
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		reader = parserFactory.newSAXParser().getXMLReader();
		ProviewSingleTitleParser singleTitleParser = parser;
		reader.setContentHandler(singleTitleParser);

	}

	@Test
	public void testParser() throws Exception {
		String response = "<titles><title id=\"uscl/an/book_lohinosubtosub\" "
				+ "version=\"v1.0\" lastupdate=\"20160425\" status=\"Final\" "
				+ "name=\"Single To Split\"/><title id=\"uscl/an/book_lohinosubtosub\" "
				+ "version=\"v1.1\" lastupdate=\"20160426\" status=\"Final\" "
				+ "name=\"Single To Split\"/></titles>";
		reader.parse(new InputSource(new StringReader(response)));

		List<GroupDetails> groupDetails = parser.getGroupDetailsList();
		Assert.assertEquals("v1.1", parser.getVersion());
		Assert.assertEquals("Final",parser.getStatus());
		Assert.assertEquals("Single To Split",parser.getName());
		Assert.assertEquals("20160426",parser.getLastUpdate());
		Assert.assertEquals(2, groupDetails.size());
	}

}
