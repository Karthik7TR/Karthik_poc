package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.File;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.service.BundlePdfsService;
import com.thomsonreuters.uscl.ereader.orchestrate.engine.web.WebConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Returns all PDFs related to selected bundle.
 */
@Controller
@Slf4j
public class BundlePdfsController {

    @Resource(name = "bundlePdfsService")
    private BundlePdfsService bundlePdfsService;

    @RequestMapping(value = WebConstants.URI_GET_BUNDLE_PDFS, method = {RequestMethod.POST, RequestMethod.GET})
    public void getBundlePdfsArchive(
        @PathVariable final String jobInstanceId,
        @PathVariable final String materialNumber,
        final HttpServletResponse response) {
        final File materialNumberDir = bundlePdfsService.getMaterialNumberDir(jobInstanceId, materialNumber);

        if (!materialNumberDir.exists()) {
            response.setStatus(410); //Gone
            return;
        }

        try (final ZipOutputStream zout = new ZipOutputStream(response.getOutputStream())) {
            bundlePdfsService.addFilesToZip(materialNumberDir, zout);
            response.setStatus(200);
        } catch (final Exception e) {
            log.error("Cannot write message to http response", e);
            response.setStatus(500);
        }
    }
}
