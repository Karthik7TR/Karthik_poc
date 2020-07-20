package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileStreamController.class, File.class, InputStream.class, FileInputStream.class, IOUtils.class})
public final class FileStreamControllerTest {
    private static final String IMAGE_FILE_TYPE = "image/png";
    private static final String CSS_FILE_TYPE = "text/css";
    private static final String MISSING_COVER_IMAGE_PATH = "/theme/images/missingCover.png";

    private FileStreamController controller;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private ServletContext servletContext;

    @Mock
    private InputStream inputStream;

    @Mock
    private FileInputStream fileInputStream;

    @Mock
    private ServletOutputStream servletOutputStream;

    @Mock
    private File file;

    private byte[] content = {1, 2, 3, 4};
    private String resourceName = "testResourceName";

    @SneakyThrows
    @Before
    public void setUp() {
        controller = new FileStreamController();

        mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toByteArray(any(InputStream.class))).thenReturn(content);

        when(response.getOutputStream()).thenReturn(servletOutputStream);
        doNothing().when(servletOutputStream).write(content);
        doNothing().when(servletOutputStream).flush();
    }

    @Ignore
    @Test
    public void testGetCoverImage() {
        controller.getCoverImage(resourceName, request, response);

        verifyWriteContent(IMAGE_FILE_TYPE, fileInputStream);
    }

    @Ignore
    @Test
    public void testGetFrontMatterImage() {
        controller.getFrontMatterImage(resourceName, request, response);

        verifyWriteContent(IMAGE_FILE_TYPE, fileInputStream);
    }

    @Ignore
    @Test
    public void testGetFrontMatterCss() {
        controller.getFrontMatterCss(resourceName, request, response);

        verifyWriteContent(CSS_FILE_TYPE, fileInputStream);
    }

    @Ignore
    @Test
    public void testGetCoverImageNoFileFound() {
        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContext);
        when(servletContext.getResourceAsStream(MISSING_COVER_IMAGE_PATH)).thenReturn(inputStream);

        controller.getCoverImage(resourceName, request, response);

        verify(request).getSession();
        verify(session).getServletContext();
        verify(servletContext).getResourceAsStream(MISSING_COVER_IMAGE_PATH);
        verifyWriteContent(IMAGE_FILE_TYPE, inputStream);
    }

    @SneakyThrows
    private void initiateMocks(final String filePath) {
        whenNew(File.class).withArguments(filePath, resourceName).thenReturn(file);
        whenNew(FileInputStream.class).withArguments(file).thenReturn(fileInputStream);
        when(file.isFile()).thenReturn(true);
    }

    @SneakyThrows
    private void verifyWriteContent(final String contentType, final InputStream inputStream) {
        verifyStatic();
        IOUtils.toByteArray(inputStream);
        verify(response).setContentType(contentType);
        verify(response).setContentLength(content.length);
        verify(servletOutputStream).write(content);
        verify(servletOutputStream).flush();
        verify(inputStream).close();
    }
}
