package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The HTMLWrapperService iterates through a directory of transformed raw HTML files and
 * wraps the raw files with proper HTML header and container tags.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLWrapperServiceImpl implements HTMLWrapperService
{
    private static final Logger LOG = LogManager.getLogger(TransformerServiceImpl.class);

    private FileHandlingHelper fileHandlingHelper;
    private KeyCiteBlockGenerationServiceImpl keyCiteBlockGenerationService;

    public void setfileHandlingHelper(final FileHandlingHelper fileHandlingHelper)
    {
        this.fileHandlingHelper = fileHandlingHelper;
    }

    public void setKeyCiteBlockGenerationService(final KeyCiteBlockGenerationServiceImpl keyCiteBlockGenerationService)
    {
        this.keyCiteBlockGenerationService = keyCiteBlockGenerationService;
    }

    /**
     * Wraps all transformed files found in the passed in transformation directory and writes the
     * properly marked up HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param transDir the directory that contains all the intermediate generated HTML files generated
     * by the Transformer Service for this eBook.
     * @param htmlDir the target directory to which all the properly marked up HTML files will be written out to.
     * @param docToTocMapping location of the file that contains the document to TOC mappings that
     * will be used to generate anchors for the TOC references
     *
     * @param titleId
     *
     * @param jobId
     *
     * @param docGuid
     *
     * @return The number of documents that had wrappers added
     *
     * @throws EBookFormatException if an error occurs during the process.
     */
    @Override
    public int addHTMLWrappers(
        final File transDir,
        final File htmlDir,
        final File docToTocMap,
        final String titleId,
        final long jobId,
        final boolean keyciteToplineFlag) throws EBookFormatException
    {
        if (transDir == null || !transDir.isDirectory())
        {
            throw new IllegalArgumentException("transDir must be a directory, not null or a regular file.");
        }

        if (docToTocMap == null || !docToTocMap.exists())
        {
            throw new IllegalArgumentException("docToTocMap must be an existing file on the system.");
        }

        //retrieve list of all transformed files that need HTML wrappers
        final List<File> transformedFiles = new ArrayList<>();

        try
        {
//			FileExtensionFilter fileExtFilter = new FileExtensionFilter();
//			fileExtFilter.setAcceptedFileExtensions(new String[]{"postunlink"}); // lowercase compare
//			fileHandlingHelper.setFilter(fileExtFilter);

            fileHandlingHelper.getFileList(transDir, transformedFiles);
        }
        catch (final FileNotFoundException e)
        {
            final String errMessage = "No transformed files were found in specified directory. "
                + "Please verify that the correct transformed path was specified.";
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }

        if (!htmlDir.exists())
        {
            htmlDir.mkdirs();
        }

        final Map<String, String[]> anchorMap = new HashMap<>();
        readTOCGuidList(docToTocMap, anchorMap);

        LOG.info("Adding HTML containers around transformed files...");

        int numDocs = 0;
        for (final File transFile : transformedFiles)
        {
            addHTMLWrapperToFile(transFile, htmlDir, anchorMap, titleId, jobId, keyciteToplineFlag);
            numDocs++;
        }

        LOG.info("HTML containers successfully added to transformed files");
        return numDocs;
    }

    /**
     * Takes in a .tranformed file and wraps it with the appropriate header and footers, the resulting file is
     * written to the specified HTML directory.
     *
     * @param transformedFile source file that will be wrapped with appropriate header and footer
     * @param htmlDir target directory the newly created file with the wrappers will be written to
     * @param anchorMap cached authority map of anchors that need to be inserted into each document
     * @param titleId
     * @param jobId
     * @param keyciteToplineFlag
     *
     * @throws EBookFormatException thrown if any IO exceptions are encountered
     */
    final void addHTMLWrapperToFile(
        final File transformedFile,
        final File htmlDir,
        final Map<String, String[]> anchorMap,
        final String titleId,
        final Long jobId,
        final boolean keyciteToplineFlag) throws EBookFormatException
    {
        final String fileName = transformedFile.getName();

        LOG.debug("Adding wrapper around: " + fileName);
        final String guid = fileName.substring(0, fileName.indexOf("."));

        final StringBuffer anchors = new StringBuffer();

        generateAnchorStream(anchorMap, guid, anchors);

        final File output = new File(htmlDir, guid + ".html");

        if (output.exists())
        {
            //delete file if it exists since the output buffer will just append on to it
            //otherwise restarting the step would double up the file
            output.delete();
        }

        try (FileOutputStream outputStream = new FileOutputStream(output, true);
            InputStream headerStream = getClass().getResourceAsStream("/StaticFiles/HTMLHeader.txt");
            InputStream transFileStream = new FileInputStream(transformedFile);
            InputStream footerStream = getClass().getResourceAsStream("/StaticFiles/HTMLFooter.txt");
            ByteArrayInputStream anchorStream = new ByteArrayInputStream(anchors.toString().getBytes());)
        {
            IOUtils.copy(headerStream, outputStream);

            IOUtils.copy(anchorStream, outputStream);

            if (keyciteToplineFlag)
            {
                try (InputStream keyciteStream = copyStream(titleId, jobId, guid, outputStream))
                {
                    //Intentionally left blank
                }
            }

            IOUtils.copy(transFileStream, outputStream);

            IOUtils.copy(footerStream, outputStream);
        }
        catch (final IOException ioe)
        {
            final String errMessage =
                "Failed to add HTML contrainers around the following transformed file: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, ioe);
        }
    }

    /**
     * @param titleId
     * @param jobId
     * @param guid
     * @param outputStream
     * @return
     * @throws EBookFormatException
     * @throws IOException
     */
    private InputStream copyStream(
        final String titleId,
        final Long jobId,
        final String guid,
        final FileOutputStream outputStream) throws EBookFormatException, IOException
    {
        final InputStream keyciteStream = keyCiteBlockGenerationService.getKeyCiteInfo(titleId, jobId, guid);
        IOUtils.copy(keyciteStream, outputStream);
        return keyciteStream;
    }

    /**
     * Reads in a list of TOC Guids that are associated to each Doc Guid to later be used
     * for anchor insertion and generates a map.
     *
     * @param docGuidsFile file containing the DOC to TOC guid relationships
     * @param docToTocGuidMap in memory map generated based on values found in the provided file
     */
    protected void readTOCGuidList(final File docGuidsFile, final Map<String, String[]> docToTocGuidMap)
        throws EBookFormatException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(docGuidsFile));)
        {
            LOG.info("Reading in TOC anchor map file...");
            int numDocs = 0;
            int numTocs = 0;

            String input = reader.readLine();
            while (input != null)
            {
                numDocs++;
                final String[] line = input.split(",", -1);
                if (!line[1].equals(""))
                {
                    final String[] tocGuids = line[1].split("\\|");
                    numTocs = numTocs + tocGuids.length;
                    docToTocGuidMap.put(line[0], tocGuids);
                }
                else
                {
                    final String message = "No TOC guid was found for the "
                        + numDocs
                        + " document. "
                        + "Please verify that each document GUID in the following file has "
                        + "at least one TOC guid associated with it: "
                        + docGuidsFile.getAbsolutePath();
                    LOG.error(message);
                    throw new EBookFormatException(message);
                }
                input = reader.readLine();
            }
            LOG.info("Generated a map for " + numDocs + " DOCs with " + numTocs + " TOC references.");
        }
        catch (final IOException e)
        {
            final String message =
                "Could not read the DOC guid to TOC guid map file: " + docGuidsFile.getAbsolutePath();
            LOG.error(message);
            throw new EBookFormatException(message, e);
        }
    }

    /**
     * Generates the anchor string that will be appended to the beginning of the document.
     *
     * @param anchorMap map containing references to the TOC nodes above the currently processing document
     * @param guid key by which the referenced TOCs will be looked up
     * @param anchorStr buffered string that will contain all the needed anchors concatenated in correct format
     * @throws EBookFormatException if one of the documents TOC references are not found.
     */
    public void generateAnchorStream(
        final Map<String, String[]> anchorMap,
        final String guid,
        final StringBuffer anchorStr) throws EBookFormatException
    {
        final String[] anchors = anchorMap.get(guid);
        if (anchors == null || anchors.length < 1)
        {
            final String errMessage = "No TOC anchor references were found for the following GUID: " + guid;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage);
        }

        for (final String anchor : anchors)
        {
            anchorStr.append("<a name=\"");
            anchorStr.append(anchor);
            anchorStr.append("\"></a>");
        }
    }
}
