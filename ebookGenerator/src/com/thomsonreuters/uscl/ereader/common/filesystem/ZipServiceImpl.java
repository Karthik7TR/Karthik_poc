package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 *Utility class to manage functionality to do common operatopns with zip archives
 */
@Component("zipService")
public class ZipServiceImpl implements ZipService
{
    private static final int BUFFER_SIZE = 4096;

    /**
     * Unpacks file from
     * @param zipFilePath to
     * @param destDirectory
     * @return directory File with unpacked archive
     */
    @NotNull
    @Override
    public File unzip(@NotNull final File zipFilePath, @NotNull final File destDirectory)
    {
        if (!destDirectory.exists())
        {
            destDirectory.mkdirs();
        }
        try (final ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));)
        {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            File file = null;
            while (entry != null)
            {
                file = new File(destDirectory, entry.getName());
                if (!entry.isDirectory())
                {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, file);
                }
                else
                {
                    // if the entry is a directory, make the directory
                    file.mkdirs();
                }
                entry = zipIn.getNextEntry();
            }
            return file;
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void extractFile(final ZipInputStream zipIn, final File file) throws IOException
    {
        try (final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)))
        {
            final byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1)
            {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
