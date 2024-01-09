package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.local.LocalXppBundleHandleService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Sends JMS message to queue to launch XppBundleQueuePoller for development purposes.
 */
@Controller
public class XppBundleQueueJmsController {
    @Resource
    private LocalXppBundleHandleService localXppBundleHandleService;

    @RequestMapping(value = "/xppbundle", method = {RequestMethod.POST, RequestMethod.GET})
    public void sendJmsMessage(@RequestParam final String materialNumber,
                                               @RequestParam final String srcFile,
                                               final HttpServletResponse response) {
        final XppBundleArchive xppBundleArchive = localXppBundleHandleService.createXppBundleArchive(materialNumber, srcFile);
        final String jmsMessage = localXppBundleHandleService.sendXppBundleJmsMessage(xppBundleArchive);
        onSuccess(response, jmsMessage);
    }

    @RequestMapping(value = "/xppbundle_folder", method = RequestMethod.GET)
    public void handleXppBundlesFolder(@RequestParam final String srcDir,
                                       final HttpServletResponse response) {
        final int handledBundlesCount = localXppBundleHandleService.processXppBundleDirectory(srcDir);
        onSuccess(response, String.format("Handled bundles count %s", handledBundlesCount));
    }

    private void onSuccess(final HttpServletResponse response, final String responseMessage) {
        try {
            response.getWriter().write(responseMessage);
            response.setStatus(200);
        } catch (final IOException e) {
            throw new RuntimeException(
                String.format("Cannot write message: %s to http response", responseMessage), e);
        }
    }
}
