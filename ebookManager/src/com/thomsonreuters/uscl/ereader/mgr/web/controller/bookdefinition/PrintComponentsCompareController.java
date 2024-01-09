package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponentHistory;
import com.thomsonreuters.uscl.ereader.request.service.PrintComponentHistoryService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PrintComponentsCompareController {
    @Autowired
    private PrintComponentHistoryService printComponentsHistoryService;
    private ObjectMapper jsonMapper = new ObjectMapper();

    @RequestMapping(value = WebConstants.MVC_PRINT_COMPONENTS_HISTORY_PANEL, method = RequestMethod.POST)
    public void getPrintComponentsLastVersion(@RequestParam("bookDefinitionId") final Long bookDefinitionId,
        final HttpServletResponse response) throws JsonProcessingException {
        final List<String> versions = printComponentsHistoryService.findPrintComponentVersionsList(bookDefinitionId);
        if (CollectionUtils.isNotEmpty(versions)) {
            final String lastVersion = versions.iterator().next();
            final Set<PrintComponentHistory> historyPrintComponents = printComponentsHistoryService.findPrintComponentByVersion(bookDefinitionId, lastVersion);
            onSuccess(response, StringEscapeUtils.escapeXml10(jsonMapper.writeValueAsString(historyPrintComponents)), HttpServletResponse.SC_OK);
        } else {
            onSuccess(response, StringUtils.EMPTY, HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @RequestMapping(value = WebConstants.MVC_PRINT_COMPONENTS_HISTORY_VERSION, method = RequestMethod.POST)
    public void getPrintComponentsByVersion(@RequestParam("bookDefinitionId") final Long bookDefinitionId,
        @RequestParam("version") final String version, final HttpServletResponse response) throws JsonProcessingException {
        final Set<PrintComponentHistory> historyPrintComponents = printComponentsHistoryService.findPrintComponentByVersion(bookDefinitionId, version);

        final int status = CollectionUtils.isNotEmpty(historyPrintComponents) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NO_CONTENT;

        onSuccess(response, StringEscapeUtils.escapeXml10(jsonMapper.writeValueAsString(historyPrintComponents)), status);
    }

    public void setPrintComponentHistoryAttributes(final Long id, final Model model) {
        final List<String> printComponentsHistoryVersions = printComponentsHistoryService.findPrintComponentVersionsList(id);
        final boolean hasPrintComponentsHistory = CollectionUtils.isNotEmpty(printComponentsHistoryVersions);
        model.addAttribute("hasPrintComponentsHistory", hasPrintComponentsHistory);
        model.addAttribute("printComponentsHistoryVersions", printComponentsHistoryVersions);
        model.addAttribute("printComponentsHistoryLastVersionNumber", hasPrintComponentsHistory ? printComponentsHistoryVersions.get(0) : "");
    }

    private void onSuccess(final HttpServletResponse response, final String responseMessage, final int status) {
        try {
            response.getWriter().write(responseMessage);
            response.setStatus(status);
        } catch (final IOException e) {
            throw new EBookException(
                String.format("Cannot write message: %s to http response", responseMessage), e);
        }
    }
}
