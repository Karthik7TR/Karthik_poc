package com.thomsonreuters.uscl.ereader.quality.helper;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@NoArgsConstructor
public class FtpManager {
    private static final int PORT = 21;

    private FTPClient ftpClient;
    private String ftpServerPath;
    private String ftpStoragePath;
    private String username;
    private String password;

    @Autowired
    public FtpManager(
        final FTPClient ftpClient,
        @Value("${xpp.quality.ftp.server}") final String ftpServerPath,
        @Value("${xpp.quality.ftp.storage}") final String ftpStoragePath,
        @Value("${xpp.quality.ftp.username}") final String username,
        @Value("${xpp.quality.ftp.password}") final String password) {
        this.ftpClient = ftpClient;
        this.ftpServerPath = ftpServerPath;
        this.ftpStoragePath = ftpStoragePath;
        this.username = username;
        this.password = password;
    }

    @SneakyThrows
    public void uploadFile(final String localFilePath) {
        try (InputStream inputStream = new FileInputStream(localFilePath)) {
            final File file = new File(localFilePath);
            ftpClient.storeFile(ftpStoragePath + file.getName(), inputStream);
        }
    }

    @SneakyThrows
    public File downloadFile(final String remoteFileName, final String localPath) {
        final String fileName = substringAfterLast(localPath, "\\");
        final String reportsDirPath = substringBeforeLast(localPath, "\\");
        final File reportsDir = new File(reportsDirPath);
        final File downloadedFile = new File(reportsDir, fileName);

        if (!reportsDir.exists()
            && !reportsDir.mkdir()) {
            throw new RuntimeException("Unable to create directories for DeltaText report file");
        }
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadedFile))) {
            ftpClient.retrieveFile(remoteFileName, outputStream);
        }
        return downloadedFile;
    }

    @SneakyThrows
    public void connect() {
        ftpClient.connect(ftpServerPath, PORT);
        ftpClient.login(username, password);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    @SneakyThrows
    public void disconnect() {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }
}
