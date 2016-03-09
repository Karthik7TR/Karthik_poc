package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.io.StringReader;
import java.math.BigDecimal;

import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SingleTitleParserTest {
	
	SingleTitleParser singleTitleParser;
	
	@Before
	public void setUp() throws Exception {
		singleTitleParser = new SingleTitleParser();
	}
	
	@Test
	public void testProviewSplitTitle() throws Exception{
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><titles>"
				+ "<title id=\"uscl/cr/az_local_qed_cwb_auto_split\" version=\"v1.0\" lastupdate=\"20160223\" status=\"Review\" name=\"Test Book Gary CWB Auto Split (eBook 1 of 2)\" />"
				+ "</titles>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		
		reader.setContentHandler(singleTitleParser);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Assert.assertEquals("Review",singleTitleParser.getStatus());	
		Assert.assertEquals("Test Book Gary CWB Auto Split",singleTitleParser.getName());	
		Assert.assertEquals("v1.0",singleTitleParser.getVersion());
		Assert.assertEquals(1,singleTitleParser.getGroupDetailsList().size());
	}
	
	@Test
	public void testProviewSingleTitle() throws Exception{
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><titles>"
				+ "<title id=\"uscl/cr/az_local_qed_cwb_auto_split\" version=\"v1.0\" lastupdate=\"20160223\" status=\"Review\" name=\"Test Book Gary CWB Auto Split\" />"
				+ "</titles>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		
		reader.setContentHandler(singleTitleParser);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Assert.assertEquals("Review",singleTitleParser.getStatus());	
		Assert.assertEquals("Test Book Gary CWB Auto Split",singleTitleParser.getName());	
		Assert.assertEquals("v1.0",singleTitleParser.getVersion());
		Assert.assertEquals(1,singleTitleParser.getGroupDetailsList().size());	
	}
	
	@Test
	public void testProviewSingleTitles() throws Exception{
		String proviewResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><titles>"
				+ "<title id=\"uscl/cr/az_local_qed_cwb_auto_split\" version=\"v1.0\" lastupdate=\"20160223\" status=\"Review\" name=\"Test Book Gary CWB Auto Split\" />"
				+ "<title id=\"uscl/cr/az_local_qed_cwb_auto_split_1\" version=\"v1.1\" lastupdate=\"20160224\" status=\"Final\" name=\"Test Book Gary CWB Auto Split_1\" />"
				+ "</titles>";
		
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		XMLReader reader = parserFactory.newSAXParser().getXMLReader();
		
		
		reader.setContentHandler(singleTitleParser);
		reader.parse(new InputSource(new StringReader(proviewResponse)));
		Assert.assertEquals(2,singleTitleParser.getGroupDetailsList().size());	

		Assert.assertEquals("Final",singleTitleParser.getGroupDetailsList().get(1).getBookStatus());	
		Assert.assertEquals("Test Book Gary CWB Auto Split_1",singleTitleParser.getGroupDetailsList().get(1).getProviewDisplayName());	
		Assert.assertEquals("v1.1",singleTitleParser.getGroupDetailsList().get(1).getBookVersion());
	}

}
