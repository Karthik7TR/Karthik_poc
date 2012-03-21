package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.NortService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;

public class NortControllerTest {
	//private static Logger log = Logger.getLogger(NortControllerTest.class);
    private NortService mockNortService;
    private NortController controller;
    
    private String domain;
	private static final String FILTER_NAME = "bogusName";
	private static final File NORTDIR_DIR = new File("NortData");

	@Before
	public void setUp() {
		this.domain =  "a";
    	this.mockNortService = EasyMock.createMock(NortService.class);
    	this.controller = new NortController();
    	controller.setNortService(mockNortService);
	}
	
	@Test
	public void testFetchNortumentsSuccessfully() throws Exception {
		File tocFile = new File(NORTDIR_DIR, "file");
		Date cutoffDate = new Date();
		GatherResponse gatherResponse = new GatherResponse();
//		mockNortService.findTableOfContents(domain, FILTER_NAME, tocFile, cutoffDate, new Long(1));
		EasyMock.expect(mockNortService.findTableOfContents(domain, FILTER_NAME, tocFile, cutoffDate)).andReturn(gatherResponse);
		EasyMock.replay(mockNortService);

    	// Invoke the controller
//    	GatherNortRequest tocRequest = new GatherNortRequest(domain, FILTER_NAME, tocFile, cutoffDate, new Long(1));
    	GatherNortRequest tocRequest = new GatherNortRequest(domain, FILTER_NAME, tocFile, cutoffDate);
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
        
        EasyMock.verify(mockNortService);
	}
	
	@Test
	public void testFetchNortumentsWithException() {
		File tocFile = new File(NORTDIR_DIR, "file");

		int errorCode = 911;
		String errorMesg = "bogus error";
		GatherException expectedException = new GatherException(errorMesg, errorCode);
		try {
//			mockNortService.findTableOfContents(domain, FILTER_NAME, tocFile, null, new Long(1));
			mockNortService.findTableOfContents(domain, FILTER_NAME, tocFile, null);
			EasyMock.expectLastCall().andThrow(expectedException);
			EasyMock.replay(mockNortService);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

    	// Invoke the controller
//    	GatherNortRequest tocRequest = new GatherNortRequest(domain, FILTER_NAME, tocFile, null, (long) 1);
    	GatherNortRequest tocRequest = new GatherNortRequest(domain, FILTER_NAME, tocFile, null);
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

    	EasyMock.verify(mockNortService);
	}
}
