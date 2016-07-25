/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;

public class TocControllerTest {
	//private static Logger log = LogManager.getLogger(TocControllerTest.class);
    private TocService mockTocService;
    private TocController controller;
    
    private String guid;
	private static final String COLLECTION_NAME = "bogusCollname";
	private static final File TOC_DIR = new File("tocData");
	private static final boolean IS_FINAL_STAGE = true;

	@Before
	public void setUp() {
		this.guid =  "a";
    	this.mockTocService = EasyMock.createMock(TocService.class);
    	this.controller = new TocController();
    	controller.setTocService(mockTocService);
	}
	
	@Test
	public void testFetchTocumentsSuccessfully() throws Exception {
		File tocFile = new File(TOC_DIR, "file");
//		Long jobId =  new Long(1);
		GatherResponse gatherResponse = new GatherResponse();
		EasyMock.expect(mockTocService.findTableOfContents(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0)).andReturn(gatherResponse);
		EasyMock.replay(mockTocService);

    	// Invoke the controller
    	GatherTocRequest tocRequest = new GatherTocRequest(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
    	Model model = new ExtendedModelMap();
    	ModelAndView mav = controller.getTableOfContents(tocRequest, model);
    	
    	// Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        Map<String,Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(0, gatherResponse.getErrorCode());
        Assert.assertNull(gatherResponse.getErrorMessage());
        
        EasyMock.verify(mockTocService);
	}
	
	@Test
	public void testFetchTocumentsWithException() {
		File tocFile = new File(TOC_DIR, "file");
		int errorCode = 911;
		String errorMesg = "bogus error";
		GatherException expectedException = new GatherException(errorMesg, errorCode);
		try {
			mockTocService.findTableOfContents(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
			EasyMock.expectLastCall().andThrow(expectedException);
			EasyMock.replay(mockTocService);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

    	// Invoke the controller
    	GatherTocRequest tocRequest = new GatherTocRequest(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
    	Model model = new ExtendedModelMap();
    	ModelAndView mav = controller.getTableOfContents(tocRequest, model);

    	// Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        Map<String,Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        GatherResponse gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(errorCode, gatherResponse.getErrorCode());
        Assert.assertEquals(errorMesg, gatherResponse.getErrorMessage());

    	EasyMock.verify(mockTocService);
	}
}
