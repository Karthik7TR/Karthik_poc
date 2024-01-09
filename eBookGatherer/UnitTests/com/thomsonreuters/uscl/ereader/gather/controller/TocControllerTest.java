package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public final class TocControllerTest {
    private TocService mockTocService;
    private TocController controller;

    private String guid;
    private static final String COLLECTION_NAME = "bogusCollname";
    private static final File TOC_DIR = new File("tocData");
    private static final boolean IS_FINAL_STAGE = true;

    @Before
    public void setUp() {
        guid = "a";
        mockTocService = EasyMock.createMock(TocService.class);
        controller = new TocController();
        controller.setTocService(mockTocService);
    }

    @Test
    public void testFetchTocumentsSuccessfully() throws Exception {
        final File tocFile = new File(TOC_DIR, "file");
//		Long jobId =  new Long(1);
        GatherResponse gatherResponse = new GatherResponse();
        EasyMock
            .expect(
                mockTocService.findTableOfContents(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0))
            .andReturn(gatherResponse);
        EasyMock.replay(mockTocService);

        // Invoke the controller
        final GatherTocRequest tocRequest =
            new GatherTocRequest(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
        final Model model = new ExtendedModelMap();
        final ModelAndView mav = controller.getTableOfContents(tocRequest, model);

        // Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        final Map<String, Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(0, gatherResponse.getErrorCode());
        Assert.assertNull(gatherResponse.getErrorMessage());

        EasyMock.verify(mockTocService);
    }

    @Test
    public void testFetchTocumentsWithException() {
        final File tocFile = new File(TOC_DIR, "file");
        final int errorCode = 911;
        final String errorMesg = "bogus error";
        final GatherException expectedException = new GatherException(errorMesg, errorCode);
        try {
            mockTocService.findTableOfContents(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
            EasyMock.expectLastCall().andThrow(expectedException);
            EasyMock.replay(mockTocService);
        } catch (final Exception e) {
            Assert.fail(e.getMessage());
        }

        // Invoke the controller
        final GatherTocRequest tocRequest =
            new GatherTocRequest(guid, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
        final Model model = new ExtendedModelMap();
        final ModelAndView mav = controller.getTableOfContents(tocRequest, model);

        // Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        final Map<String, Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        final GatherResponse gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(errorCode, gatherResponse.getErrorCode());
        Assert.assertEquals(errorMesg, gatherResponse.getErrorMessage());

        EasyMock.verify(mockTocService);
    }
}
