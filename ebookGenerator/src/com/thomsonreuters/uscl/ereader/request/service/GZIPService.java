package com.thomsonreuters.uscl.ereader.request.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.thomsonreuters.uscl.ereader.request.XppMessageException;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

public class GZIPService
{
    private static final int BUFF_SIZE = 2048;
    private static final Logger LOG = LogManager.getLogger(GZIPService.class);

    public void untarzip(final File tarball, final File destDir) throws XppMessageException
    {
        try (TarInputStream tarIn =
            new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(tarball)))))
        {
            TarEntry entry = null;
            // Read the tar entries using the getNextEntry method
            while ((entry = tarIn.getNextEntry()) != null)
            {
                LOG.debug("Extracting: " + entry.getName());
                final File tarEntryFile = new File(destDir, entry.getName());
                if (entry.isDirectory())
                {
                    // create directory
                    tarEntryFile.mkdirs();
                }
                else
                {
                    // write file
                    int count;
                    final byte[] data = new byte[BUFF_SIZE];
                    try (BufferedOutputStream dest =
                        new BufferedOutputStream(new FileOutputStream(tarEntryFile), BUFF_SIZE))
                    {
                        while ((count = tarIn.read(data, 0, BUFF_SIZE)) != -1)
                        {
                            dest.write(data, 0, count);
                        }
                        dest.close();
                    }
                    catch (final IOException e)
                    {
                        final String message =
                            String.format(XPPConstants.ERROR_EXTRACT_FILES, tarEntryFile.getAbsolutePath());
                        LOG.error(message, e);
                        throw new XppMessageException(message, e);
                    }
                }
            }
            LOG.debug("Completed unzip of " + tarball.getPath());
        }
        catch (final IOException e)
        {
            final String message = String.format(XPPConstants.ERROR_TARBALL_NOT_FOUND, tarball.getAbsolutePath());
            LOG.error(message, e);
            throw new XppMessageException(message, e);
        }
    }

    public void tarzip(final File srcDir, final File dest) throws IOException
    {
        try (TarOutputStream tarout =
            new TarOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(dest), BUFF_SIZE))))
        {
            recursivelyTarZip(tarout, srcDir.getAbsolutePath(), StringUtils.EMPTY);
            LOG.debug("unzip complete");
        }
        catch (final IOException e)
        {
            final String message = "Unable to create tarball at indicated location  " + dest.getAbsolutePath();
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    // Following methods copied from EBookAssemblyServiceImpl
    // TODO migrate tar methods from EBookAssemblyService to a shared tarball utility (this class)

    /**
     * Recursively searches through a directory and creates a tar entry for each file and
     * folder.
     *
     * @param tarOutputStream - the TarOutputStream where each file will be streamed to.
     * @param path - the path to the directory to be added to the archive
     * @param base - the path prefix to be used for each entry within the archive
     *
     * @throws IOException
     */
    private void recursivelyTarZip(final TarOutputStream tarOutputStream, final String path, final String base)
        throws IOException
    {
        final File targetFile = new File(path);
        final String entryName = base + targetFile.getName();

        addTarEntry(tarOutputStream, targetFile, entryName);

        if (targetFile.isFile())
        {
            writeTarFileToArchive(tarOutputStream, targetFile);
        }
        else
        {
            try
            {
                tarOutputStream.closeEntry();
            }
            catch (final IOException e)
            {
                final String message = "error while attempting to flush TarOutputStream";
                LOG.error(message, e);

                throw new IOException(message, e);
            }

            final File[] children = targetFile.listFiles();

            if (children != null)
            {
                for (final File file : children)
                {
                    recursivelyTarZip(tarOutputStream, file.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }

    /**
     * Copies the contents of a file into the stream that represents the TAR file.
     *
     * @param tarOutputStream the TarOutputStream that will be written to disk (at some point).
     * @param targetFile the file to be added to the archive.
     *
     * @throws IOException if an underlying FileNotFoundException or IOException occurs.
     */
    private void writeTarFileToArchive(final TarOutputStream tarOutputStream, final File targetFile) throws IOException
    {
        try
        {
            IOUtils.copy(new FileInputStream(targetFile), tarOutputStream);
            tarOutputStream.closeEntry();
        }
        catch (final FileNotFoundException e)
        {
            final String message =
                "Could not write contents of file to TarOutputStream. Does the input file exist at the specified location? "
                    + "Are read permissions enabled on the file: "
                    + targetFile.getName()
                    + "?";
            LOG.error(message, e);
            throw new IOException(message, e);
        }
        catch (final IOException e)
        {
            final String message = "Could not close entry in tar file.";
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }

    /**
     * Adds an entry to the TAR file.
     *
     * @param tarOutputStream the outputStream that will eventually be written out.
     * @param targetFile the file to be added to the archive.
     * @param entryName the name of the file within the archive.
     *
     * @throws IOException if an IOException occurs during this process.
     */
    private void addTarEntry(final TarOutputStream tarOutputStream, final File targetFile, final String entryName)
        throws IOException
    {
        final TarEntry tarEntry = new TarEntry(targetFile);
        tarEntry.setName(entryName);

        try
        {
            LOG.debug("Adding TAREntry: " + tarEntry.getName());
            tarOutputStream.putNextEntry(tarEntry);
        }
        catch (final IOException e)
        {
            final String message = "An exception occurred while preparing file header to write.";
            LOG.error(message, e);
            throw new IOException(message, e);
        }
    }
}
