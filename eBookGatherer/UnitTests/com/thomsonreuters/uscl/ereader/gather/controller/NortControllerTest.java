package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.NortService;

public class NortControllerTest {
	//private static Logger log = Logger.getLogger(NortControllerTest.class);
    private NortService mockNortService;
    private NortController controller;
    
    private String domain;
	private static final String FILTER_NAME = "bogusName";
	private static final File NORTDIR_DIR = new File("NortData");
	private static final boolean IS_FINAL_STAGE = true;
	private static final boolean USE_RELOAD_CONTENT = true;

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
		EasyMock.expect(mockNortService.findTableOfContents(domain, FILTER_NAME, tocFile, cutoffDate, null, null, IS_FINAL_STAGE, USE_RELOAD_CONTENT, null, 0)).andReturn(gatherResponse);
		EasyMock.replay(mockNortService);

    	// Invoke the controller
    	GatherNortRequest tocRequest = new GatherNortRequest(domain, FILTER_NAME, tocFile, cutoffDate, null, null, IS_FINAL_STAGE, USE_RELOAD_CONTENT, null, 0);
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
			mockNortService.findTableOfContents(domain, FILTER_NAME, tocFile, null, null, null, IS_FINAL_STAGE, USE_RELOAD_CONTENT, null, 0);
			EasyMock.expectLastCall().andThrow(expectedException);
			EasyMock.replay(mockNortService);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

    	// Invoke the controller
    	GatherNortRequest tocRequest = new GatherNortRequest(domain, FILTER_NAME, tocFile, null, null, null, IS_FINAL_STAGE, USE_RELOAD_CONTENT, null, 0);
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
