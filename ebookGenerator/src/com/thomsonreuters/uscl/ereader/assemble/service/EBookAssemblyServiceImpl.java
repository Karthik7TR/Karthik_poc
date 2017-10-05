package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.thomsonreuters.uscl.ereader.assemble.exception.EBookAssemblyException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

/**
 * EBookAssemblyServiceImpl is responsible for creating a valid compressed tarball from whichever directory is passed as input to the assembleEBook method.
 *
 * <p>This class makes use of recursion to create the archive and cannot cope with circular directory structures.
 * This class is not responsible for determining that the eBook format, which ProView expects, is correct.</p>
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class EBookAssemblyServiceImpl implements EBookAssemblyService {
    private static final Logger LOG = LogManager.getLogger(EBookAssemblyServiceImpl.class);

    /**
     * Assembles an eBook given an eBookDirectory where the files reside and a file to stream the output to.
     *
     * @param eBookDirectory the input directory that contains the eBook structure (artwork, assets, documents directories and title.xml)
     * @param eBook the title that is created as a result of the process.
     * @throws EBookAssemblyException if something unexpected happens during the process.
     */
    @Override
    public final void assembleEBook(final File eBookDirectory, final File eBook) throws EBookAssemblyException {
        if (eBookDirectory == null || !eBookDirectory.isDirectory()) {
            throw new IllegalArgumentException("eBookDirectory must be a directory, not null or a regular file.");
        }
        if (eBook == null || eBook.isDirectory()) {
            throw new IllegalArgumentException("eBook must be a regular file, not null or a directory.");
        }

        LOG.debug("Assembling eBook using the input directory: " + eBookDirectory.getAbsolutePath());

        try (TarOutputStream tarOutputStream =
            new TarOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(eBook))));) {
            tarOutputStream.setLongFileMode(TarOutputStream.LONGFILE_GNU);

            recursivelyTarDirectory(tarOutputStream, eBookDirectory.getAbsolutePath(), "");
            tarOutputStream.flush();
        } catch (final FileNotFoundException e) {
            final String message = "Destination file is inaccesable or does not exist. Is write permission set on "
                + eBook.getName()
                + "?";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        } catch (final IOException e) {
            final String message = "Failed to flush the TarOutputStream to disk.  Is the disk full?";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        }
    }

    /**
     * gets list of documents in Document folder
     * @param contentFolderPath
     * @return
     */
    @Override
    public long getDocumentCount(final String contentFolderPath) {
        long docCount = 0;
        final File contentDir = new File(contentFolderPath);
        final File[] fileList = contentDir.listFiles();
        docCount = fileList.length;
        return docCount;
    }

    /**
     * gets back largest content file size for passed in file path and content type (file Extension, may contain multiple csv extensions)
     */
    @Override
    public long getLargestContent(final String contentFolderPath, final String fileExtension) {
        final String[] extensions;
        extensions = fileExtension.split(",");

        long largestFileSize = 0;
        final File contentDri = new File(contentFolderPath);
        final File[] fileList = contentDri.listFiles();
        for (final File file : fileList) { //every file in directory
            for (final String extend : extensions) { //every extension input
                if (file.getAbsolutePath().endsWith(extend)) {
                    if (largestFileSize < file.length()) {
                        largestFileSize = file.length();
                    }
                    break; //file extension identified, no need to check more
                }
            }
        }

        return largestFileSize;
    }

    /**
     * Recursively searches through a directory and creates a tar entry for each file and
     * folder.
     *
     * @param tarOutputStream - the TarOutputStream where each file will be streamed to.
     * @param path - the path to the directory to be added to the archive
     * @param base - the path prefix to be used for each entry within the archive
     *
     * @throws EBookAssemblyException
     */
    final void recursivelyTarDirectory(final TarOutputStream tarOutputStream, final String path, final String base)
        throws EBookAssemblyException {
        final File targetFile = new File(path);
        final String entryName = base + targetFile.getName();

        addTarEntry(tarOutputStream, targetFile, entryName);

        if (targetFile.isFile()) {
            writeTarFileToArchive(tarOutputStream, targetFile);
        } else {
            try {
                tarOutputStream.closeEntry();
            } catch (final IOException e) {
                final String message = "error while attempting to flush TarOutputStream";
                LOG.error(message, e);

                throw new EBookAssemblyException(message, e);
            }

            final File[] children = targetFile.listFiles();

            if (children != null) {
                for (final File file : children) {
                    recursivelyTarDirectory(tarOutputStream, file.getAbsolutePath(), entryName + "/");
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
     * @throws EBookAssemblyException if an underlying FileNotFoundException or IOException occurs.
     */
    final void writeTarFileToArchive(final TarOutputStream tarOutputStream, final File targetFile)
        throws EBookAssemblyException {
        try {
            IOUtils.copy(new FileInputStream(targetFile), tarOutputStream);
            tarOutputStream.closeEntry();
        } catch (final FileNotFoundException e) {
            final String message =
                "Could not write contents of file to TarOutputStream. Does the input file exist at the specified location? "
                    + "Are read permissions enabled on the file: "
                    + targetFile.getName()
                    + "?";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        } catch (final IOException e) {
            final String message = "Could not close entry in tar file.";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        }
    }

    /**
     * Adds an entry to the TAR file.
     *
     * @param tarOutputStream the outputStream that will eventually be written out.
     * @param targetFile the file to be added to the archive.
     * @param entryName the name of the file within the archive.
     *
     * @throws EBookAssemblyException if an IOException occurs during this process.
     */
    final void addTarEntry(final TarOutputStream tarOutputStream, final File targetFile, final String entryName)
        throws EBookAssemblyException {
        final TarEntry tarEntry = new TarEntry(targetFile);
        tarEntry.setName(entryName);

        try {
            LOG.debug("Adding TAREntry: " + tarEntry.getName());
            tarOutputStream.putNextEntry(tarEntry);
        } catch (final IOException e) {
            final String message = "An exception occurred while preparing file header to write.";
            LOG.error(message, e);
            throw new EBookAssemblyException(message, e);
        }
    }
}
