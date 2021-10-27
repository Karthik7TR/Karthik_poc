package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.CW_PUBLISHER_NAME;

/**
 * Controller used to stream files from NAS location to the web application.
 */

@Slf4j
@Controller
@Validated
public class FileStreamController {
    private static final String FILE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "File with name %s was not found on server";

    @Autowired
    private NasFileSystem nasFileSystem;

    @RequestMapping(value = CoreConstants.MVC_COVER_IMAGE, method = RequestMethod.GET)
    public void getCoverImage(
        @RequestParam("imageName") final String imageName,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        retrieveFile(request, response, nasFileSystem.getCoverImagesDirectory().getAbsolutePath(), imageName, "image/png");
    }

    @RequestMapping(value = CoreConstants.MVC_FRONT_MATTER_IMAGE_PREVIEW, method = RequestMethod.GET)
    public void getFrontMatterImagePreview(
        @RequestParam("imageName") final String imageName,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        retrieveFile(request, response, nasFileSystem.getFrontMatterImagesDirectory().getAbsolutePath(), imageName, "image/png");
    }

    @RequestMapping(value = CoreConstants.MVC_FRONT_MATTER_CSS_PREVIEW, method = RequestMethod.GET)
    public void getFrontMatterCssPreview(
        @RequestParam("cssName") final String cssName,
        final HttpServletRequest request,
        final HttpServletResponse response) {
        retrieveFile(request, response, nasFileSystem.getFrontMatterCssDirectory().getAbsolutePath(), cssName, "text/css");
    }

    @RequestMapping(value = CoreConstants.MVC_FRONT_MATTER_PDF_PREVIEW, method = RequestMethod.GET)
    public void getFrontMatterPdfPreview(
            @RequestParam("pdfName") @Valid @NotBlank(message = "pdfName may not be blank") final String pdfName,
            @RequestParam("publisher") @Valid @NotBlank(message = "publisher may not be blank") final String publisher,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        Optional.ofNullable(getFrontMatterDirectory(pdfName, publisher, response))
                .ifPresent(frontMatterDirectory -> retrieveFile(request, response, frontMatterDirectory.getAbsolutePath(), pdfName, "application/pdf"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("Not valid due to validation error(s): " + getErrorMessage(e), HttpStatus.BAD_REQUEST);
    }

    @NotNull
    private String getErrorMessage(final ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
    }

    @Nullable
    private File getFrontMatterDirectory(final String pdfName, final String publisher, final HttpServletResponse response) {
        File frontMatterDirectory;
        if (CW_PUBLISHER_NAME.equals(publisher)) {
            File pdfFile = new File(nasFileSystem.getFrontMatterCwPdfDirectory(), pdfName);
            if (pdfFile.exists()) {
                frontMatterDirectory = nasFileSystem.getFrontMatterCwPdfDirectory();
            } else {
                frontMatterDirectory = getFileInUsclDir(pdfName, response);
            }
        } else {
            frontMatterDirectory = getFileInUsclDir(pdfName, response);
        }
        return frontMatterDirectory;
    }

    @Nullable
    @SneakyThrows
    private File getFileInUsclDir(final String pdfName, final HttpServletResponse response) {
        File frontMatterDirectory;
        File pdfFile = new File(nasFileSystem.getFrontMatterUsclPdfDirectory(), pdfName);
        if (pdfFile.exists()) {
            frontMatterDirectory = nasFileSystem.getFrontMatterUsclPdfDirectory();
        } else {
            ServletOutputStream out = response.getOutputStream();
            out.write(String.format(FILE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, pdfName).getBytes());
            out.flush();
            return null;
        }
        return frontMatterDirectory;
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
            if (!file.isFile() && nasLocation.equalsIgnoreCase(nasFileSystem.getCoverImagesDirectory().getAbsolutePath())) {
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
