/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;

/**
 * Test the XML image tag handler.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XMLImageTagHandlerTest {

    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
    
	private SAXParserFactory factory;
	private SAXParser saxParser;
	private XMLImageTagHandler imgTagHandler;
	private List<String> emptyGuidList;
	private List<String> guidList;
	
	private String xmlWithImgTags = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table " +
			"used to determine the guideline range follows:</paratext>" +
			"<image.block><image.link target=\"I5d463990094d11e085f5891ac64a9905\" /></image.block>" +
			"<image.block><image.link target=\"I5d463990094d11e085f5891ac64a9906\" /></image.block>" +
			"<eos /><eop /></para></primary.notes>";

	private String xmlWithoutImgTags = "<n-docbody><doc bill=\"Y\"><content.metadata.block><cmd.identifiers>" +
			"<cmd.cites><cmd.second.line.cite><bop /><bos />Fed. Sent. L. and Prac. Ch. 5 Pt. A (2011 ed.)" +
			"<eos /><eop /></cmd.second.line.cite></cmd.cites></cmd.identifiers></content.metadata.block>" +
			"</doc></n-docbody>";
	
	private String xmlWithImgTagsNoTargetAtt = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table " +
			"used to determine the guideline range follows:</paratext>" +
			"<image.block><image.link target=\"I5d463990094d11e085f5891ac64a9905\" /></image.block>" +
			"<image.block><image.link /></image.block>" +
			"<eos /><eop /></para></primary.notes>";
	
	private File xmlWithTags;
	private File xmlWithoutTags;
	private File xmlWithTagsNoTargetAtt;
	
	@Before
	public void setUp() throws Exception
	{
		imgTagHandler = new XMLImageTagHandler();
		emptyGuidList = new ArrayList<String>();
		guidList = new ArrayList<String>();
		guidList.add("I5d463990094d11e085f5891ac64a9905");
		guidList.add("I8A302FE4920F47B00079B5381C71638B");
		
		xmlWithTags = testFiles.newFile("xmlWithTags.xml");
    	OutputStream outputStream = new FileOutputStream(xmlWithTags);
		outputStream.write(xmlWithImgTags.getBytes());
		outputStream.flush();
		outputStream.close();
		
		xmlWithoutTags = testFiles.newFile("xmlWithoutTags.xml");
    	OutputStream outputStream2 = new FileOutputStream(xmlWithoutTags);
		outputStream2.write(xmlWithoutImgTags.getBytes());
		outputStream2.flush();
		outputStream2.close();
		
		xmlWithTagsNoTargetAtt = testFiles.newFile("xmlWithOddTags.xml");
    	OutputStream outputStream3 = new FileOutputStream(xmlWithTagsNoTargetAtt);
		outputStream3.write(xmlWithImgTagsNoTargetAtt.getBytes());
		outputStream3.flush();
		outputStream3.close();
		
		factory = SAXParserFactory.newInstance();
		saxParser = factory.newSAXParser();
	}
	
	@Test
	public void testSetterGetter()
	{
		imgTagHandler.setGuidList(guidList);
		assertEquals(2, (imgTagHandler.getGuidList()).size());
	}
	
	@Test
	public void testStartElementNotTriggered() throws Exception
	{		
		imgTagHandler.setGuidList(emptyGuidList);
			
		saxParser.parse(xmlWithoutTags, imgTagHandler);
		assertEquals(0, imgTagHandler.getGuidList().size());
	}
	
	@Test
	public void testStartElementTriggered() throws Exception
	{
		imgTagHandler.setGuidList(emptyGuidList);
			
		saxParser.parse(xmlWithTags, imgTagHandler);
		assertEquals(2, imgTagHandler.getGuidList().size());
	}
	
	@Test
	public void testStartElementTriggeredWithExistingList() throws Exception
	{
		imgTagHandler.setGuidList(guidList);
			
		saxParser.parse(xmlWithTags, imgTagHandler);
		assertEquals(4, imgTagHandler.getGuidList().size());
	}
	
	@Test
	public void testStartElementTriggeredWithoutTargetAttribute() throws Exception
	{
		imgTagHandler.setGuidList(emptyGuidList);			
		saxParser.parse(xmlWithTagsNoTargetAtt, imgTagHandler);
		assertEquals(2, emptyGuidList.size());
		assertEquals(null, emptyGuidList.get(1));
	}
}
