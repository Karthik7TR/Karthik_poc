package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.DocService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public final class DocControllerTest
{
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
    public void setUp()
    {
        final String[] guidArray = {"a", "b", "c"};
        guids = new ArrayList<>(Arrays.asList(guidArray));
        mockDocService = EasyMock.createMock(DocService.class);
//    	this.controller = EasyMock.createMock(DocController.class);
        controller = new DocController();
        controller.setDocService(mockDocService);
    }

    @Test
    public void testFetchDocumentsSuccessfully() throws Exception
    {
        final GatherResponse gatherResponse1 = new GatherResponse();
        EasyMock
            .expect(
                mockDocService.fetchDocuments(
                    guids,
                    COLLECTION_NAME,
                    CONTENT_DIR,
                    METADATADIR_DIR,
                    IS_FINAL_STAGE,
                    USE_RELOAD_CONTENT))
            .andReturn(gatherResponse1);
        EasyMock.replay(mockDocService);

        // Invoke the controller
        final GatherDocRequest docRequest = new GatherDocRequest(
            guids,
            COLLECTION_NAME,
            CONTENT_DIR,
            METADATADIR_DIR,
            IS_FINAL_STAGE,
            USE_RELOAD_CONTENT);
        final Model model = new ExtendedModelMap();
        final ModelAndView mav = controller.fetchDocuments(docRequest, model);

        // Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        final Map<String, Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        final GatherResponse gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(0, gatherResponse.getErrorCode());
        Assert.assertNull(gatherResponse.getErrorMessage());
        EasyMock.verify(mockDocService);
    }

    @Test
    public void testFetchDocumentsWithException()
    {
        final int errorCode = 911;
        final String errorMesg = "bogus error";
        final GatherException expectedException = new GatherException(errorMesg, errorCode);
        try
        {
            mockDocService.fetchDocuments(
                guids,
                COLLECTION_NAME,
                CONTENT_DIR,
                METADATADIR_DIR,
                IS_FINAL_STAGE,
                USE_RELOAD_CONTENT);
            EasyMock.expectLastCall().andThrow(expectedException);
            EasyMock.replay(mockDocService);
        }
        catch (final Exception e)
        {
            Assert.fail(e.getMessage());
        }

        // Invoke the controller
        final GatherDocRequest docRequest = new GatherDocRequest(
            guids,
            COLLECTION_NAME,
            CONTENT_DIR,
            METADATADIR_DIR,
            IS_FINAL_STAGE,
            USE_RELOAD_CONTENT);
        final Model model = new ExtendedModelMap();
        final ModelAndView mav = controller.fetchDocuments(docRequest, model);

        // Verify the state created by the controller for the GET http request
        assertNotNull(mav);
        assertEquals(EBConstants.VIEW_RESPONSE, mav.getViewName());
        final Map<String, Object> modelMap = model.asMap();
        Assert.assertNotNull(modelMap);
        final GatherResponse gatherResponse = (GatherResponse) modelMap.get(EBConstants.GATHER_RESPONSE_OBJECT);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(errorCode, gatherResponse.getErrorCode());
        Assert.assertEquals(errorMesg, gatherResponse.getErrorMessage());

        EasyMock.verify(mockDocService);
    }
}
