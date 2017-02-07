package com.thomsonreuters.uscl.ereader.gather.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Date;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.NortService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public final class NortControllerTest
{
    //private static Logger log = LogManager.getLogger(NortControllerTest.class);
    private NortService mockNortService;
    private NortController controller;

    private String domain;
    private static final String FILTER_NAME = "bogusName";
    private static final File NORTDIR_DIR = new File("NortData");
    private static final boolean IS_FINAL_STAGE = true;
    private static final boolean USE_RELOAD_CONTENT = true;

    @Before
    public void setUp()
    {
        domain = "a";
        mockNortService = EasyMock.createMock(NortService.class);
        controller = new NortController();
        controller.setNortService(mockNortService);
    }

    @Test
    public void testFetchNortumentsSuccessfully() throws Exception
    {
        final File tocFile = new File(NORTDIR_DIR, "file");
        final Date cutoffDate = new Date();
        GatherResponse gatherResponse = new GatherResponse();
        EasyMock
            .expect(
                mockNortService.findTableOfContents(
                    domain,
                    FILTER_NAME,
                    tocFile,
                    cutoffDate,
                    null,
                    null,
                    IS_FINAL_STAGE,
                    USE_RELOAD_CONTENT,
                    null,
                    0))
            .andReturn(gatherResponse);
        EasyMock.replay(mockNortService);

        // Invoke the controller
        final GatherNortRequest tocRequest = new GatherNortRequest(
            domain,
            FILTER_NAME,
            tocFile,
            cutoffDate,
            null,
            null,
            IS_FINAL_STAGE,
            USE_RELOAD_CONTENT,
            null,
            0);
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

        EasyMock.verify(mockNortService);
    }

    @Test
    public void testFetchNortumentsWithException()
    {
        final File tocFile = new File(NORTDIR_DIR, "file");

        final int errorCode = 911;
        final String errorMesg = "bogus error";
        final GatherException expectedException = new GatherException(errorMesg, errorCode);
        try
        {
            mockNortService.findTableOfContents(
                domain,
                FILTER_NAME,
                tocFile,
                null,
                null,
                null,
                IS_FINAL_STAGE,
                USE_RELOAD_CONTENT,
                null,
                0);
            EasyMock.expectLastCall().andThrow(expectedException);
            EasyMock.replay(mockNortService);
        }
        catch (final Exception e)
        {
            Assert.fail(e.getMessage());
        }

        // Invoke the controller
        final GatherNortRequest tocRequest = new GatherNortRequest(
            domain,
            FILTER_NAME,
            tocFile,
            null,
            null,
            null,
            IS_FINAL_STAGE,
            USE_RELOAD_CONTENT,
            null,
            0);
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

        EasyMock.verify(mockNortService);
    }
}
