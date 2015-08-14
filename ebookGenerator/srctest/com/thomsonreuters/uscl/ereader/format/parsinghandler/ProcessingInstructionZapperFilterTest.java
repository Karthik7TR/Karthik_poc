/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Assert;

import org.apache.commons.io.IOUtils;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

/**
 * Tests for the ProcessingInstructionZapperFilter
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class ProcessingInstructionZapperFilterTest {

	InputSource inputSource;
	ProcessingInstructionZapperFilter processingInstructionZapperfilter;
	SAXParser saxParser;
	ContentHandler contentHandler;
	ByteArrayOutputStream transformedXmlOutputStream;
	
	@Before
	public void setUp() throws Exception {
	    processingInstructionZapperfilter = new ProcessingInstructionZapperFilter();
		saxParser = SAXParserFactory.newInstance().newSAXParser();
		
		transformedXmlOutputStream = new ByteArrayOutputStream();
		Properties outputFormat = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
		outputFormat.setProperty("omit-xml-declaration", "yes");
		
		Serializer serializer = SerializerFactory.getSerializer(outputFormat);
		serializer.setOutputStream(transformedXmlOutputStream);
		contentHandler = serializer.asContentHandler();
	}
	
	@Test
	public void testProcessingInstructionsAreZapped() throws Exception {
		String inputXml = "<yarr><?pirate?></yarr>";
		String expectedXml = "<yarr/>";
		inputSource = new InputSource(new ByteArrayInputStream(inputXml.getBytes()));
		processingInstructionZapperfilter.setParent(saxParser.getXMLReader());
		processingInstructionZapperfilter.setContentHandler(contentHandler);
		processingInstructionZapperfilter.parse(inputSource);
		String actualXml = IOUtils.toString(transformedXmlOutputStream.toByteArray(), "UTF-8");
		Assert.assertTrue("Expected [" + expectedXml + "] but was [" + actualXml + "].", expectedXml.equals(actualXml));
	}
	
	@Test
	public void testXmlContentIsPreserved() throws Exception {
		String inputXml = "<yarr><?pirate?><ship>Black Pearl</ship></yarr>";
		String expectedXml = "<yarr><ship>Black Pearl</ship></yarr>";
		inputSource = new InputSource(new ByteArrayInputStream(inputXml.getBytes()));
		processingInstructionZapperfilter.setParent(saxParser.getXMLReader());
		processingInstructionZapperfilter.setContentHandler(contentHandler);
		processingInstructionZapperfilter.parse(inputSource);
		String actualXml = IOUtils.toString(transformedXmlOutputStream.toByteArray(), "UTF-8");
		Assert.assertTrue("Expected [" + expectedXml + "] but was [" + actualXml + "].", expectedXml.equals(actualXml));
	}
	
	@Test
	public void testUnicodeDecimalEntityReferences() throws Exception {
		String inputXml = "<foo><?pirate?>bar&#167;<?pirate?></foo>";
		String sectionSymbol = new String(new byte[]{(byte) 0xC2, (byte) 0xA7}, "UTF8");
		String expectedXml = "<foo>bar" + sectionSymbol + "</foo>";
		inputSource = new InputSource(new ByteArrayInputStream(inputXml.getBytes()));
		processingInstructionZapperfilter.setParent(saxParser.getXMLReader());
		processingInstructionZapperfilter.setContentHandler(contentHandler);
		processingInstructionZapperfilter.parse(inputSource);
		String actualXml = IOUtils.toString(transformedXmlOutputStream.toByteArray(), "UTF-8");
		Assert.assertTrue("Expected [" + expectedXml + "] but was [" + actualXml + "].", expectedXml.equals(actualXml));
	}
}
