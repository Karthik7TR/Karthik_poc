/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;

import org.easymock.EasyMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test various HTMLAnchorFilter data scenarios.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLAnchorFilterTest {

	private HTMLAnchorFilter anchorFilter;
	private Serializer serializer;
	private final long testJobId = 123L;
	private final String testGuid = "NFA730F80D58A11DCBFA7F697EE59258B";
	private final String invalidGuid = "badGuid";
	private final String firstlineCite = "ABC";
	
	private ImageMetadataEntity regularImgMetadata;
	private ImageMetadataEntity largeImgMetadata;
	private ImageMetadataEntity largeHeightImgMetadata;
	private ImageMetadataEntity largeWidthImgMetadata;
	
	@Before
	public void setUp() throws Exception
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		
		anchorFilter = new HTMLAnchorFilter();
		anchorFilter.setjobInstanceId(testJobId);
		anchorFilter.setFirstlineCite(firstlineCite);
		anchorFilter.setParent(saxParser.getXMLReader());
		
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
		props.setProperty("omit-xml-declaration", "yes");
		serializer = SerializerFactory.getSerializer(props);
	
		regularImgMetadata = new ImageMetadataEntity();
		regularImgMetadata.setHeight(200L);
		regularImgMetadata.setWidth(200L);
	
		largeImgMetadata = new ImageMetadataEntity();
		largeImgMetadata.setHeight(2048L);
		largeImgMetadata.setWidth(2048L);
	
		largeHeightImgMetadata = new ImageMetadataEntity();
		largeHeightImgMetadata.setHeight(670L);
		largeHeightImgMetadata.setWidth(200L);
	
		largeWidthImgMetadata = new ImageMetadataEntity();
		largeWidthImgMetadata.setHeight(200L);
		largeWidthImgMetadata.setWidth(650L);
	}
	
	@After
	public void tearDown() throws Exception
	{
		serializer = null;
		anchorFilter = null;
	}
	
	/** 
	 * Helper method that sets up the repeating pieces of each test and modifies the ImageService
	 * values that are returned along with the input and output.
	 * 
	 * @param imgService image service object that returns image metadata
	 * @param inputXML input string for the test.
	 * @param expectedResult the expected output for the specified input string.
	 */
	public void testHelper(ImageService imgService, String inputXML, String expectedResult) throws SAXException
	{
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output = null;
		try
		{
			input = new ByteArrayInputStream(inputXML.getBytes());
			output = new ByteArrayOutputStream();
			
			serializer.setOutputStream(output);
			
			anchorFilter.setimgService(imgService);
			anchorFilter.setContentHandler(serializer.asContentHandler());
			anchorFilter.parse(new InputSource(input));
			
			String result = output.toString();
			
			assertEquals(expectedResult, result);
		}
		catch (SAXException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			fail("Encountered exception during test: " + e.getMessage());
		}
		finally
		{
			try
			{
				if (input != null)
				{
					input.close();
				}
				if (output != null)
				{
					output.close();
				}
			}
			catch (Exception e)
			{
				fail("Could clean up resources: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testSimpleImageAnchorTag() throws SAXException
	{
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleImageAnchorTagWithV1URI() throws SAXException
	{
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/v1/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	

	@Test(expected=SAXException.class)
	public void testSimpleImageAnchorTagWithInvalidGuidV1URI() throws SAXException
	{		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/v1/" + invalidGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleLargeHeightImageAnchorTag() throws SAXException
	{
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(largeHeightImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\" class=\"tr_image\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleLargeWidthImageAnchorTag() throws SAXException
	{
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(largeWidthImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\" class=\"tr_image\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleLargeImageAnchorTag() throws SAXException
	{
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(largeImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\" class=\"tr_image\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test(expected=SAXException.class)
	public void testImageAnchorTagWithInvalidGuid() throws SAXException
	{
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + invalidGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\" class=\"tr_image\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleEmptyAnchorTag() throws SAXException
	{
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><a href=\"#\"/></test>";
		String expectedResult = "<test/>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleEmptyAnchorWithContentTag() throws SAXException
	{
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><a href=\"#\">Test123</a></test>";
		String expectedResult = "<test/>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleEmptyAnchorWithOtherAttributes() throws SAXException
	{
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><a href=\"#\" class=\"test\">Test123</a></test>";
		String expectedResult = "<test/>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}

	
	@Test
	public void testSimpleNonEmptyAnchorWithOtherAttributes() throws SAXException
	{
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><a href=\"#co_test\" class=\"test\">Test123</a></test>";
		String expectedResult = "<test><a href=\"#co_test\" class=\"test\">Test123</a></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSimpleAnchorNotStripped() throws SAXException
	{
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><a id=\"co_Test\">Test123</a></test>";
		String expectedResult = "<test><a id=\"co_Test\">Test123</a></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testNoAnchorInput() throws SAXException
	{
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		
		String xmlTestStr = "<test><testing/><testing123>123</testing123></test>";
		String expectedResult = "<test><testing/><testing123>123</testing123></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testSetMaxHeight()
	{
		long newMaxHeight = 23L;
		anchorFilter.setImgMaxHeight(newMaxHeight);
		assertEquals(newMaxHeight, anchorFilter.getImgMaxHeight());
	}
	
	@Test
	public void testSetMaxWidth()
	{
		long newMaxWidth = 27L;
		anchorFilter.setImgMaxWidth(newMaxWidth);
		assertEquals(newMaxWidth, anchorFilter.getImgMaxWidth());
	}
	
	@Test
	public void testWithModifiedHeight() throws SAXException
	{
		long newMaxHeight = 27L;
		anchorFilter.setImgMaxWidth(newMaxHeight);
		
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\" class=\"tr_image\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testWithModifiedWidth() throws SAXException
	{
		long newMaxWidth = 27L;
		anchorFilter.setImgMaxWidth(newMaxWidth);
		
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\" class=\"tr_image\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
	
	@Test
	public void testMultipleAnchors() throws SAXException
	{		
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(testJobId, testGuid);
		ImageMetadataEntityKey key2 = new ImageMetadataEntityKey(testJobId, "AFA730F80D58A11DCBFA7F697EE59258B");
		
		ImageService mockImgService = EasyMock.createMock(ImageService.class);
		EasyMock.expect(mockImgService.findImageMetadata(key)).andReturn(regularImgMetadata);
		EasyMock.expect(mockImgService.findImageMetadata(key2)).andReturn(largeImgMetadata);
		EasyMock.replay(mockImgService);
		
		String xmlTestStr = "<test><a href=\"http://www.test/Blob/" + testGuid + 
				".jpg?\" type=\"image/jpeg\"/><a href=\"http://www.test/Blob/" +
				"AFA730F80D58A11DCBFA7F697EE59258B.jpg?\" type=\"image/jpeg\"/>" +
				"<a href=\"#\" class=\"test\"/></test>";
		String expectedResult = "<test><img alt=\"Image 1 within " + firstlineCite + " document.\" src=\"er:#" + 
				testGuid + "\"/><img alt=\"Image 2 within " + firstlineCite + " document.\" " +
						"src=\"er:#AFA730F80D58A11DCBFA7F697EE59258B\" class=\"tr_image\"/></test>";
		
		testHelper(mockImgService, xmlTestStr, expectedResult);
	}
}
