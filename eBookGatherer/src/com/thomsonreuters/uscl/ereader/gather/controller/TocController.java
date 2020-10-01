package com.thomsonreuters.uscl.ereader.gather.controller;

import java.io.File;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.services.TocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class TocController {
    @Autowired
    @Qualifier("tocServiceMockImpl")
    private TocService tocService;

    /**
     * Fetch the table of contents XML document.
     */
    @RequestMapping(value = "/toc", method = RequestMethod.POST)
    public ModelAndView getTableOfContents(@RequestBody final GatherTocRequest tocRequest, final Model model) {
        log.debug(">>> " + tocRequest);
        GatherResponse gatherResponse;

        // Retrieve TOC structure from Novus
        try {
            final File tocXmlFile = tocRequest.getTocFile();

            gatherResponse = tocService.findTableOfContents(
                tocRequest.getGuid(),
                tocRequest.getCollectionName(),
                tocXmlFile,
                tocRequest.getExcludeDocuments(),
                tocRequest.getRenameTocEntries(),
                tocRequest.isFinalStage(),
                tocRequest.getSplitTocGuidList(),
                tocRequest.getThresholdValue());
            // Create EBook TOC file on specified path
        } catch (final GatherException e) {
            gatherResponse = new GatherResponse(e.getErrorCode(), getErrorMessage(e));
        } catch (final Exception e) {
            gatherResponse = new GatherResponse(GatherResponse.CODE_UNHANDLED_ERROR, getErrorMessage(e));
        }

        model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
        return new ModelAndView(EBConstants.VIEW_RESPONSE);
    }

    public void setTocService(final TocService service) {
        tocService = service;
    }

    private String getErrorMessage(final Exception e) {
        String errorMessage = e.getMessage();
        final Throwable cause = e.getCause();
        if (cause != null) {
            errorMessage = errorMessage + " - " + cause.getMessage();
        }
        log.error(errorMessage, e);
        return errorMessage;
    }
}
