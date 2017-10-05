package com.thomsonreuters.uscl.ereader.common.service.compress;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

/**
 *Utility class to manage functionality to do common operatopns with zip archives
 */
@Service("zipService")
public class ZipServiceImpl implements CompressService {
    private static final int BUFFER_SIZE = 4096;

    @Override
    public void decompress(final File zipFilePath, final File destDirectory, final String pathToSkip) throws Exception {
        if (!destDirectory.exists()) {
            destDirectory.mkdirs();
        }
        try (final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));) {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            File file = null;
            while (entry != null) {
                file = new File(destDirectory, entry.getName().replaceFirst(pathToSkip, StringUtils.EMPTY));
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, file);
                } else {
                    // if the entry is a directory, make the directory
                    file.mkdirs();
                }
                entry = zipIn.getNextEntry();
            }
        }
    }

    /**
     * Unpacks file from
     * @param zipFilePath to
     * @param destDirectory
     * @return directory File with unpacked archive
     * @throws Exception
     */
    @Override
    public void decompress(@NotNull final File zipFilePath, @NotNull final File destDirectory) throws Exception {
        decompress(zipFilePath, destDirectory, StringUtils.EMPTY);
    }

    private void extractFile(final ZipInputStream zipIn, final File file) throws IOException {
        try (final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            final byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    @Override
    public void compress(final File srcDir, final File dest) throws Exception {
        throw new UnsupportedOperationException("Compression operation not supported for zip archives");
    }
}
