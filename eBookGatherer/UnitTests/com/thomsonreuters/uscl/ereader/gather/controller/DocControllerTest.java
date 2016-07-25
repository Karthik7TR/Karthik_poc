/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.DocService;

public class DocControllerTest {
	//private static Logger log = LogManager.getLogger(DocControllerTest.class);
    private DocService mockDocService;
    private DocController controller;
    
    private Collection<String> guids;
	private static final String COLLECTION_NAME = "bogusCollname";
	private static final File CONTENT_DIR = new File("docContent");
	private static final File METADATADIR_DIR = new File("docMetadata");
	private static final boolean IS_FINAL_STAGE = true;
	private static final boolean USE_RELOAD_CONTENT = true;
	
	@Before
	public void setUp() {
		String[] guidArray = { "a", "b", "c" };
		this.guids = new ArrayList<String>(Arrays.asList(guidArray));
    	this.mockDocService = EasyMock.createMock(DocService.class);
//    	this.controller = EasyMock.createMock(DocController.class);
    	this.controller = new DocController();
    	controller.setDocService(mockDocService);
	}
	
	@Test
	public void testFetchDocumentsSuccessfully() throws Exception {
		
		GatherResponse gatherResponse1 = new GatherResponse();
		EasyMock.expect(mockDocService.fetchDocuments(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR, IS_FINAL_STAGE, USE_RELOAD_CONTENT)).andReturn(gatherResponse1);
		EasyMock.replay(mockDocService);
		
    	// Invoke the controller
    	GatherDocRequest docRequest = new GatherDocRequest(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR, IS_FINAL_STAGE, USE_RELOAD_CONTENT);
    	Model model = new ExtendedModelMap();
    	ModelAndView mav = controller.fetchDocuments(docRequest, model);
    	
    	// Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        Map<String,Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        GatherResponse gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(0, gatherResponse.getErrorCode());
        Assert.assertNull(gatherResponse.getErrorMessage());       
        EasyMock.verify(mockDocService);

	}
	
	@Test
	public void testFetchDocumentsWithException() {
		int errorCode = 911;
		String errorMesg = "bogus error";
		GatherException expectedException = new GatherException(errorMesg, errorCode);
		try {
			mockDocService.fetchDocuments(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR, IS_FINAL_STAGE, USE_RELOAD_CONTENT);
			EasyMock.expectLastCall().andThrow(expectedException);
			EasyMock.replay(mockDocService);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

    	// Invoke the controller
    	GatherDocRequest docRequest = new GatherDocRequest(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR, IS_FINAL_STAGE, USE_RELOAD_CONTENT);
    	Model model = new ExtendedModelMap();
    	ModelAndView mav = controller.fetchDocuments(docRequest, model);

    	// Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        Map<String,Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        GatherResponse gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(errorCode, gatherResponse.getErrorCode());
        Assert.assertEquals(errorMesg, gatherResponse.getErrorMessage());

    	EasyMock.verify(mockDocService);
	}
}
