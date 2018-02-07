package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystemImpl;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "bundlePdfsService")
public class BundlePdfsService {
    private static final int BUFFER_SIZE = 512;
    private static final FileFilter PDF_FILE = file -> file.getName().toLowerCase().endsWith("pdf");
    private static final FileFilter PDF_DIRECTORY = file -> file.isDirectory() && file.getName().equals("PDF");

    @Value("${root.work.directory}")
    private File rootWorkDirectory;
    @Resource(name = "environmentName")
    private String environmentName;

    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;

    @Resource(name = "bookDefinitionService")
    private BookDefinitionService bookDefinitionService;

    @NotNull
    public File getMaterialNumberDir(@NotNull final String jobInstanceId, @NotNull final String materialNumber) {
        final String dynamicPath = getDynamicPath(jobInstanceId, materialNumber);
        return new File(rootWorkDirectory, dynamicPath);
    }

    public void addFilesToZip(@NotNull final File materialNumberDir, @NotNull final ZipOutputStream zout) throws IOException {
        final File pdfDir = getPdfDir(materialNumberDir);

        for (final File pdfFile : pdfDir.listFiles(PDF_FILE)) {
            addFileToZip(pdfDir, pdfFile, zout);
        }
    }

    private String getDynamicPath(final String jobInstanceId, final String materialNumber) {
        final PublishingStats stats = Optional.ofNullable(publishingStatsService.findPublishingStatsByJobId(Long.valueOf(jobInstanceId)))
            .orElseThrow(() -> new EBookException("Can not find jobInstanceId " + jobInstanceId));

        final String dateBasedDir = BookFileSystemImpl.getDateBasedDirName(stats.getJobSubmitTimestamp());
        final Long bookDefinitionId = stats.getEbookDefId();
        final BookDefinition boodDefinition = bookDefinitionService.findBookDefinitionByEbookDefId(bookDefinitionId);
        final String titleId = boodDefinition.getTitleId();

        return String.format("%s/%s/%s/%s/%s/Gather/Bundles/%s", environmentName, CoreConstants.DATA_DIR, dateBasedDir, titleId, jobInstanceId, materialNumber);
    }

    private File getPdfDir(final File materialNumberDir) {
        return Stream.of(materialNumberDir.listFiles(File::isDirectory))
            .map(folder -> Stream.of(folder.listFiles(PDF_DIRECTORY))
                .findFirst()
                .orElseThrow(() -> new EBookException("Expect " + materialNumberDir.getAbsolutePath() + "/[bundle name]/PDF/ dir.")))
            .findFirst()
            .orElseThrow(() -> new EBookException("Expect " + materialNumberDir.getAbsolutePath() + "/[bundle name]/ dir."));
    }

    private void addFileToZip(final File parentFolder, final File file, final ZipOutputStream zout)
        throws IOException {
        final String dirpath = parentFolder.getAbsolutePath();
        final String filepath = file.getAbsolutePath();
        final String entryName = filepath.substring(dirpath.length() + 1).replace('\\', '/');
        final ZipEntry zipEntry = new ZipEntry(entryName);
        zipEntry.setTime(file.lastModified());
        try (final FileInputStream ins = new FileInputStream(file)) {
            zout.putNextEntry(zipEntry);
            pipe(ins, zout);
            zout.closeEntry();
        }
    }

    /**
     * Pipes an input stream to an output stream.
     */
    private static void pipe(final InputStream ins, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];

        int len = ins.read(buffer);
        while (len != -1) {
            out.write(buffer, 0, len);
            len = ins.read(buffer);
        }
        out.flush();
    }
}
