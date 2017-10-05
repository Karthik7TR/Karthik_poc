package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller used to stream files from NAS location to the web application.
 */
@Controller
public class FileStreamController {
    private static final Logger log = LogManager.getLogger(AppExceptionController.class);

    @RequestMapping(value = WebConstants.MVC_COVER_IMAGE, method = RequestMethod.GET)
    public void getCoverImage(
        @RequestParam("imageName") final String imageName,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        retrieveFile(request, response, WebConstants.LOCATION_COVER_IMAGE, imageName, "image/png");
    }

    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_IMAGE, method = RequestMethod.GET)
    public void getFrontMatterImage(
        @RequestParam("imageName") final String imageName,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        retrieveFile(request, response, WebConstants.LOCATION_FRONT_MATTER_IMAGE, imageName, "image/png");
    }

    @RequestMapping(value = WebConstants.MVC_FRONT_MATTER_CSS, method = RequestMethod.GET)
    public void getFrontMatterCss(
        @RequestParam("cssName") final String cssName,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        retrieveFile(request, response, WebConstants.LOCATION_FRONT_MATTER_CSS, cssName, "text/css");
    }

    private void retrieveFile(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final String nasLocation,
        final String filename,
        final String mediaType) {
        InputStream fin = null;
        byte[] content = null;

        final File file = new File(nasLocation, filename);

        try {
            if (!file.isFile() && nasLocation.equalsIgnoreCase(WebConstants.LOCATION_COVER_IMAGE)) {
                final ServletContext ctx = request.getSession().getServletContext();
                fin = ctx.getResourceAsStream("/theme/images/missingCover.png");
            } else {
                fin = new FileInputStream(file);
            }

            content = IOUtils.toByteArray(fin);
            response.setContentType(mediaType);
            response.setContentLength(content.length);

            final ServletOutputStream out = response.getOutputStream();
            out.write(content);
            out.flush();
        } catch (final Exception e) {
            log.error("Error streaming file: ", e);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (final Exception e) {
                log.error("Error closing input stream: ", e);
            }
        }
    }
}
