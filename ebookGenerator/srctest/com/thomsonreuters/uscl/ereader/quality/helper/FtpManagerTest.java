package com.thomsonreuters.uscl.ereader.quality.helper;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.write;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class FtpManagerTest {
    private static final String FTP_STORAGE_PATH = "some/ftp/path";

    @InjectMocks
    private FtpManager sut;
    @Mock
    private FTPClient ftpClient;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        Whitebox.setInternalState(sut, "ftpStoragePath", FTP_STORAGE_PATH);
        Whitebox.setInternalState(sut, "ftpServerPath", "server");
        Whitebox.setInternalState(sut, "username", "some-username");
        Whitebox.setInternalState(sut, "password", "some-password");
        doNothing().when(ftpClient).connect(anyString(), anyInt());
        when(ftpClient.login(anyString(), anyString())).thenReturn(true);
        doNothing().when(ftpClient).enterLocalPassiveMode();
        when(ftpClient.setFileType(anyInt())).thenReturn(true);
        when(ftpClient.isConnected()).thenReturn(true);
        when(ftpClient.storeFile(anyString(), any())).thenReturn(true);
    }

    @After
    public void cleanUp() {
        folder.delete();
    }

    @Test
    public void shouldUploadFile() throws IOException {
        //given
        final File file = folder.newFile();
        final String filename = file.getName();
        final String localFilePath = file.getAbsolutePath();
        //when
        sut.uploadFile(localFilePath);
        //
        verify(ftpClient).storeFile(eq(FTP_STORAGE_PATH + filename), any());
    }

    @Test
    public void shouldDownloadFile() throws IOException {
        //given
        final File tempFile = folder.newFile();
        final String expectedContents = "downloaded";
        when(ftpClient.retrieveFile(eq(FTP_STORAGE_PATH), any())).thenAnswer(invocationOnMock -> {
            write(tempFile, expectedContents);
            return true;
        });
        //when
        final File result = sut.downloadFile(FTP_STORAGE_PATH, tempFile.getAbsolutePath());
        //then
        String actualContents = "";
        try {
            actualContents = readFileToString(result);
        } catch (final IOException e) {
            fail(e.getMessage());
        }
        assertEquals(expectedContents, actualContents);
    }
}
