/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Tests for the PlaceholderDocumentService.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class PlaceholderDocumentServiceImplTest {

	private PlaceholderDocumentServiceImpl placeholderDocumentService;
	private static final String TEST_PLACEHOLDER_DOCUMENT_TEMPLATE_LOCATION = "Tortuga";
	ResourceLoader mockResourceLoader;
	Resource mockPlaceholderDocumentResource;
	
	@Before
	public void setUp() throws Exception {
		mockResourceLoader = EasyMock.createMock(ResourceLoader.class);
		mockPlaceholderDocumentResource = EasyMock.createMock(Resource.class);
		ByteArrayInputStream testDocumentTemplateInputStream = new ByteArrayInputStream("<html><head/><body><displaytext/></body></html>".getBytes());
		EasyMock.expect(mockPlaceholderDocumentResource.getInputStream()).andReturn(testDocumentTemplateInputStream);
		EasyMock.expect(mockResourceLoader.getResource(TEST_PLACEHOLDER_DOCUMENT_TEMPLATE_LOCATION)).andReturn(mockPlaceholderDocumentResource);
		placeholderDocumentService = new PlaceholderDocumentServiceImpl();
		placeholderDocumentService.setPlaceholderDocumentTemplateLocation(TEST_PLACEHOLDER_DOCUMENT_TEMPLATE_LOCATION);
		placeholderDocumentService.setResourceLoader(mockResourceLoader);
	}
	
	@After
	public void tearDown() throws Exception {
		
	}

	private void replayAll() {
		EasyMock.replay(mockPlaceholderDocumentResource);
		EasyMock.replay(mockResourceLoader);
	}
	
	private void verifyAll() {
		EasyMock.verify(mockPlaceholderDocumentResource);
		EasyMock.verify(mockResourceLoader);
	}
	
	@Test
	public void placeholderDocumentServiceImplHappyPath() throws Exception {
		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		replayAll();
		placeholderDocumentService.generatePlaceholderDocument(resultStream, "YARR!", "tocGuid");
		verifyAll();
		String expected = "<html><head/><body>YARR!</body></html>";
		Assert.assertArrayEquals(expected.getBytes(), resultStream.toByteArray());
	}

	@Test(expected=IllegalArgumentException.class)
	public void placeholderDocumentServiceThrowsIllegalArgumentExceptionNullFirstArgument() throws Exception {
		replayAll();
		placeholderDocumentService.generatePlaceholderDocument(null, "YARR!", "tocGuid");
		verifyAll();
	}

	@Test(expected=IllegalArgumentException.class)
	public void placeholderDocumentServiceThrowsIllegalArgumentExceptionNullSecondArgument() throws Exception {
		replayAll();
		placeholderDocumentService.generatePlaceholderDocument(new ByteArrayOutputStream(), null, "tocGuid");
		verifyAll();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void placeholderDocumentServiceThrowsIllegalArgumentExceptionNullThirdArgument() throws Exception {
		replayAll();
		placeholderDocumentService.generatePlaceholderDocument(new ByteArrayOutputStream(), "YARR!", null);
		verifyAll();
	}
	@Test(expected=IllegalArgumentException.class)
	public void testBadlyConfiguredTemplateLocation() throws Exception {
		ResourceLoader resourceLoader = EasyMock.createMock(ResourceLoader.class);
		EasyMock.expect(resourceLoader.getResource(null)).andThrow(new NullPointerException("ARR! YE NULL BE POINTY, LANDLUBBER!"));
		placeholderDocumentService = new PlaceholderDocumentServiceImpl();
		placeholderDocumentService.setPlaceholderDocumentTemplateLocation(null);
		placeholderDocumentService.setResourceLoader(resourceLoader);
		
		EasyMock.replay(resourceLoader);
		placeholderDocumentService.generatePlaceholderDocument(new ByteArrayOutputStream(), "YARR!", "tocGuid");
		EasyMock.verify(resourceLoader);
	}
	
	@Test(expected=PlaceholderDocumentServiceException.class)
	public void testIOExceptionCausesPlaceholderDocumentServiceExceptionToBeThrown() throws Exception {
		ResourceLoader resourceLoader = EasyMock.createMock(ResourceLoader.class);
		Resource resource = EasyMock.createMock(Resource.class);
		EasyMock.expect(resource.getInputStream()).andThrow(new IOException("WALK THE PLANK!"));
		EasyMock.expect(resourceLoader.getResource(TEST_PLACEHOLDER_DOCUMENT_TEMPLATE_LOCATION)).andReturn(resource);
		placeholderDocumentService = new PlaceholderDocumentServiceImpl();
		placeholderDocumentService.setPlaceholderDocumentTemplateLocation(TEST_PLACEHOLDER_DOCUMENT_TEMPLATE_LOCATION);
		placeholderDocumentService.setResourceLoader(resourceLoader);
		
		EasyMock.replay(resourceLoader);
		EasyMock.replay(resource);
		placeholderDocumentService.generatePlaceholderDocument(new ByteArrayOutputStream(), "YARR!", "tocGuid");
		EasyMock.verify(resourceLoader);
		EasyMock.verify(resource);
	}
}
