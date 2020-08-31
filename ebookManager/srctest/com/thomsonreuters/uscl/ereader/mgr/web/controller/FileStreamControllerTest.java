package com.thomsonreuters.uscl.ereader.mgr.web.controller;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileStreamController.class, File.class, InputStream.class, FileInputStream.class, IOUtils.class})
public final class FileStreamControllerTest {
    private static final String IMAGE_FILE_TYPE = "image/png";
    private static final String CSS_FILE_TYPE = "text/css";
    private static final String PDF_FILE_TYPE = "application/pdf";
    private static final String MISSING_COVER_IMAGE_PATH = "/theme/images/missingCover.png";
    private static final String ERROR_MESSAGE = "File with name testResourceName was not found on server";
    private static final String ABSOLUTE_PATH = "";

    @InjectMocks
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

    @Mock
    private File file2;

    @Mock
    private NasFileSystem nasFileSystem;

    @Mock
    private File rootDir;

    @Mock
    private File rootDir2;


    private byte[] content = {1, 2, 3, 4};
    private String resourceName = "testResourceName";

    @SneakyThrows
    @Before
    public void setUp() {
        mockStatic(IOUtils.class);
        PowerMockito.when(IOUtils.toByteArray(any(InputStream.class))).thenReturn(content);

        when(response.getOutputStream()).thenReturn(servletOutputStream);
        doNothing().when(servletOutputStream).write(content);
        doNothing().when(servletOutputStream).flush();
    }

    @Test
    public void testGetCoverImage() {
        initMock(true);
        PowerMockito.when(nasFileSystem.getCoverImagesDirectory()).thenReturn(rootDir);

        controller.getCoverImage(resourceName, request, response);

        verifyWriteContent(IMAGE_FILE_TYPE, fileInputStream);
    }

    @Test
    public void testGetFrontMatterImage() {
        initMock(true);
        PowerMockito.when(nasFileSystem.getFrontMatterImagesDirectory()).thenReturn(rootDir);

        controller.getFrontMatterImagePreview(resourceName, request, response);

        verifyWriteContent(IMAGE_FILE_TYPE, fileInputStream);
    }

    @Test
    public void testGetFrontMatterCss() {
        initMock(true);
        PowerMockito.when(nasFileSystem.getFrontMatterCssDirectory()).thenReturn(rootDir);

        controller.getFrontMatterCssPreview(resourceName, request, response);

        verifyWriteContent(CSS_FILE_TYPE, fileInputStream);
    }

    @Test
    public void testGetCoverImageNoFileFound() {
        initMock(false);
        PowerMockito.when(nasFileSystem.getCoverImagesDirectory()).thenReturn(rootDir);

        when(request.getSession()).thenReturn(session);
        when(session.getServletContext()).thenReturn(servletContext);
        when(servletContext.getResourceAsStream(MISSING_COVER_IMAGE_PATH)).thenReturn(fileInputStream);

        controller.getCoverImage(resourceName, request, response);

        verify(request).getSession();
        verify(session).getServletContext();
        verify(servletContext).getResourceAsStream(MISSING_COVER_IMAGE_PATH);
        verifyWriteContent(IMAGE_FILE_TYPE, fileInputStream);
    }

    @Test
    public void testGetFrontMatterPdfUscl() {
        initMock(true);
        initFile(when(nasFileSystem.getFrontMatterUsclPdfDirectory()), rootDir, file, true);

        controller.getFrontMatterPdfPreview(resourceName, CoreConstants.USCL_PUBLISHER_NAME, request, response);

        verifyWriteContent(PDF_FILE_TYPE, fileInputStream);
    }

    @Test
    public void testGetFrontMatterPdfUsclNotFound() {
        initMock(true);
        initFile(when(nasFileSystem.getFrontMatterUsclPdfDirectory()), rootDir, file, false);

        controller.getFrontMatterPdfPreview(resourceName, CoreConstants.USCL_PUBLISHER_NAME, request, response);

        verifyError();
    }

    @Test
    public void testGetFrontMatterPdfCw() {
        initMock(true);
        initFile(when(nasFileSystem.getFrontMatterCwPdfDirectory()), rootDir, file, true);

        controller.getFrontMatterPdfPreview(resourceName, CoreConstants.CW_PUBLISHER_NAME, request, response);

        verifyWriteContent(PDF_FILE_TYPE, fileInputStream);
    }

    @Test
    public void testGetFrontMatterPdfCwFileInUsclFolder() {
        initMock(true);
        initFile(when(nasFileSystem.getFrontMatterUsclPdfDirectory()), rootDir, file, true);
        initFile(when(nasFileSystem.getFrontMatterCwPdfDirectory()), rootDir2, file2, false);

        controller.getFrontMatterPdfPreview(resourceName, CoreConstants.CW_PUBLISHER_NAME, request, response);

        verifyWriteContent(PDF_FILE_TYPE, fileInputStream);
    }

    @Test
    public void testGetFrontMatterPdfCwFileNotFound() {
        initMock(true);
        initFile(when(nasFileSystem.getFrontMatterUsclPdfDirectory()), rootDir, file, false);
        initFile(when(nasFileSystem.getFrontMatterCwPdfDirectory()), rootDir2, file2, false);

        controller.getFrontMatterPdfPreview(resourceName, CoreConstants.CW_PUBLISHER_NAME, request, response);

        verifyError();
    }

    @SneakyThrows
    private void initMock(final boolean isFile) {
        whenNew(File.class).withArguments(ABSOLUTE_PATH, resourceName).thenReturn(file);
        whenNew(FileInputStream.class).withArguments(file).thenReturn(fileInputStream);
        when(rootDir.getAbsolutePath()).thenReturn(ABSOLUTE_PATH);
        when(file.isFile()).thenReturn(isFile);
    }

    @SneakyThrows
    private void initFile(final OngoingStubbing<File> stubbing, final File rootDir, final File file, final boolean isExist) {
        stubbing.thenReturn(rootDir);
        whenNew(File.class).withArguments(rootDir, resourceName).thenReturn(file);
        when(file.exists()).thenReturn(isExist);
    }

    @SneakyThrows
    private void verifyError() {
        verify(response.getOutputStream()).write(ERROR_MESSAGE.getBytes());
        verify(response.getOutputStream()).flush();
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
