package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.DocService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;

public class DocControllerTest {
	//private static Logger log = Logger.getLogger(DocControllerTest.class);
    private DocService mockDocService;
    private DocController controller;
    
    private Collection<String> guids;
	private static final String COLLECTION_NAME = "bogusCollname";
	private static final File CONTENT_DIR = new File("docContent");
	private static final File METADATADIR_DIR = new File("docMetadata");

	@Before
	public void setUp() {
		String[] guidArray = { "a", "b", "c" };
		this.guids = new ArrayList<String>(Arrays.asList(guidArray));
    	this.mockDocService = EasyMock.createMock(DocService.class);
    	this.controller = new DocController();
    	controller.setDocService(mockDocService);
	}
	
	@Test
	public void testFetchDocumentsSuccessfully() throws Exception {
		mockDocService.fetchDocuments(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR);
		EasyMock.replay(mockDocService);

    	// Invoke the controller
    	GatherDocRequest docRequest = new GatherDocRequest(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR);
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
			mockDocService.fetchDocuments(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR);
			EasyMock.expectLastCall().andThrow(expectedException);
			EasyMock.replay(mockDocService);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

    	// Invoke the controller
    	GatherDocRequest docRequest = new GatherDocRequest(guids, COLLECTION_NAME, CONTENT_DIR, METADATADIR_DIR);
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
