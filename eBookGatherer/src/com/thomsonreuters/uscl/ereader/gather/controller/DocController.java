package com.thomsonreuters.uscl.ereader.gather.controller;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.DocService;
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
public class DocController
{
    private static Logger LOG = LogManager.getLogger(DocController.class);

    private DocService docService;

    @RequestMapping(value = "/doc", method = RequestMethod.POST)
    public ModelAndView fetchDocuments(@RequestBody final GatherDocRequest docRequest, final Model model)
    {
        LOG.debug(">>> " + docRequest);
        GatherResponse gatherResponse = new GatherResponse();
        try
        {
            gatherResponse = docService.fetchDocuments(
                docRequest.getGuids(),
                docRequest.getCollectionName(),
                docRequest.getContentDestinationDirectory(),
                docRequest.getMetadataDestinationDirectory(),
                docRequest.getIsFinalStage(),
                docRequest.getUseReloadContent());
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
            gatherResponse.setErrorCode(e.getErrorCode());
            gatherResponse.setErrorMessage(errorMessage);
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
            gatherResponse.setErrorCode(GatherResponse.CODE_UNHANDLED_ERROR);
            gatherResponse.setErrorMessage(errorMessage);
        }
        model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
        return new ModelAndView(EBConstants.VIEW_RESPONSE);
    }

    @Required
    public void setDocService(final DocService service)
    {
        docService = service;
    }
}
