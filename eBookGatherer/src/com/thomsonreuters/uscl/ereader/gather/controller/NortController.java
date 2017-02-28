package com.thomsonreuters.uscl.ereader.gather.controller;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.NortService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NortController
{
    private static Logger LOG = LogManager.getLogger(NortController.class);

    private NortService nortService;

    /**
     * Fetch the table of contents XML document.
     */
    @RequestMapping(value = "/nort", method = RequestMethod.POST)
    public ModelAndView getTableOfContents(@RequestBody final GatherNortRequest nortRequest, final Model model)
    {
        LOG.debug(">>> " + nortRequest);
        GatherResponse gatherResponse = new GatherResponse();

        // Retrieve NORT TOC structure from Novus
        try
        {
            // Create EBook TOC file on specified path
            final File nortXmlFile = nortRequest.getNortFile();

            gatherResponse = nortService.findTableOfContents(
                nortRequest.getDomainName(),
                nortRequest.getExpressionFilter(),
                nortXmlFile,
                nortRequest.getCutoffDate(),
                nortRequest.getExcludeDocuments(),
                nortRequest.getRenameTocEntries(),
                nortRequest.isFinalStage(),
                nortRequest.getUseReloadContent(),
                nortRequest.getSplitTocGuidList(),
                nortRequest.getThresholdValue());
        }
        catch (final GatherException e)
        {
            String errorMessage = e.getMessage();
            final Throwable cause = e.getCause();
            if (cause != null)
            {
                errorMessage = errorMessage + " - " + cause.getMessage();
            }
            LOG.error(errorMessage, e);
            gatherResponse = new GatherResponse(e.getErrorCode(), errorMessage);
        }
        catch (final Exception e)
        {
            String errorMessage = e.getMessage();
            final Throwable cause = e.getCause();
            if (cause != null)
            {
                errorMessage = errorMessage + " - " + cause.getMessage();
            }
            LOG.error(errorMessage, e);
            gatherResponse = new GatherResponse(GatherResponse.CODE_UNHANDLED_ERROR, errorMessage);
        }
        model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
        return new ModelAndView(EBConstants.VIEW_RESPONSE);
    }

    @Required
    public void setNortService(final NortService service)
    {
        nortService = service;
    }
}
