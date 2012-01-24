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
import com.thomsonreuters.uscl.ereader.gather.services.DocService;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;

public class DocControllerTest {
	//private static Logger log = Logger.getLogger(DocControllerTest.class);
    private DocService mockDocService;
    private DocController controller;
	
	@Before
	public void setUp() {
    	this.mockDocService = EasyMock.createMock(DocService.class);
    	this.controller = new DocController();
    	controller.setDocService(mockDocService);
	}
	
	@Test
	public void testFetchDocuments() throws Exception {
		
		String[] guidArray = { "a", "b", "c" };
		Collection<String>  guids = new ArrayList<String>(Arrays.asList(guidArray));
		String collectionName = "bogusCollname";
		File contentDir = new File("foo");
		File metadataDir = new File("bar");
		
		mockDocService.fetchDocuments(guids, collectionName, contentDir, metadataDir);
		EasyMock.replay(mockDocService);

    	// Invoke the controller
    	GatherDocRequest docRequest = new GatherDocRequest(guids, collectionName, contentDir, metadataDir);
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
}
