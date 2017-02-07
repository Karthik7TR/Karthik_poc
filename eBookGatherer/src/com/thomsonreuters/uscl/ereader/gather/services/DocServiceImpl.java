package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.westgroup.novus.productapi.Document;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * NOVUS document and metadata fetch.
 * @author Nirupam Chatterjee
 * @author Ray Cracauer
 * @modified 3/9/2012 - Retry looping fix was added to prevent infinite loops.
 */
public class DocServiceImpl implements DocService
{
    private static final int META_COUNTER = 1;

    private static final int META_RETRY_COUNTER = 0;

    private static final Logger Log = LogManager.getLogger(DocServiceImpl.class);

    private NovusFactory novusFactory;

    private NovusUtility novusUtility;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.thomsonreuters.uscl.ereader.gather.services.DocService#fetchDocuments
     * (java.util.Collection, java.lang.String, java.io.File, java.io.File)
     */
    @Override
    public GatherResponse fetchDocuments(
        final Collection<String> docGuids,
        final String collectionName,
        final File contentDestinationDirectory,
        final File metadataDestinationDirectory,
        final boolean isFinalStage,
        final boolean useReloadContent) throws GatherException
    {
        Assert.isTrue(
            contentDestinationDirectory.exists(),
            "The content destination directory for the documents does not exist: " + contentDestinationDirectory);
        Assert.isTrue(
            contentDestinationDirectory.exists(),
            "The content destination directory for the document metadata does not exist: "
                + metadataDestinationDirectory);

        final GatherResponse gatherResponse = new GatherResponse();

        Novus novus = null;
        try
        {
            novus = novusFactory.createNovus(isFinalStage);
        }
        catch (final NovusException e)
        {
            final GatherException ge = new GatherException(
                "Novus error occurred while creating Novus object " + e,
                GatherResponse.CODE_NOVUS_ERROR);
            throw ge;
        }

        final Integer docRetryCount = new Integer(novusUtility.getDocRetryCount());
        boolean anyException = false;
        String docGuid = null;
        // This is the counter for checking how many Novus retries we
        // are making
        Integer novusRetryCounter = 0;
        Integer docFoundCounter = 0;
        int[] metaDocCounters = {0, 0};
        Integer metaDocFoundCounter = 0;
        Integer metaDocRetryCounter = 0;

        String publishStatus = "DOC Gather Step Completed";

        FileOutputStream stream = null;
        Writer writer = null;

        int missingGuidsCount = 0;
        final int missingMetaDataCount = 0;

        try
        {
            stream = new FileOutputStream(
                StringUtils.substringBeforeLast(
                    contentDestinationDirectory.getAbsolutePath(),
                    System.getProperty("file.separator")) + "_doc_missing_guids.txt");
            final String charset = "UTF-8"; // explicitly set the character set
            writer = new OutputStreamWriter(stream, charset);

            final Find finder = novus.getFind();
            finder.setResolveIncludes(true);
            finder.setUseReloadContent(useReloadContent);

            int tocSequence = 0;
            for (final String guid : docGuids)
            {
                tocSequence++;
                docGuid = guid;
                Document document = null;

                while (novusRetryCounter < docRetryCount)
                {
                    try
                    {
                        if (collectionName == null)
                        {
                            document = finder.getDocument(null, guid);
                        }
                        else
                        {
                            document = finder.getDocument(collectionName, null, guid);
                        }
                        break;
                    }
                    catch (final Exception exception)
                    {
                        try
                        {
                            novusRetryCounter =
                                novusUtility.handleException(exception, novusRetryCounter, docRetryCount);

                            if (novusUtility.getShowMissDocsList().equalsIgnoreCase("Y"))
                            {
                                if (novusRetryCounter == 3)
                                {// just write it once after 3 retries
                                    Log.debug(
                                        "Text is not found for the guid "
                                            + document.getGuid()
                                            + " and the error code is "
                                            + document.getErrorCode());
                                    writer.write(document.getGuid());
                                    writer.write("\n");
                                    missingGuidsCount++;
                                }
                            }
                        }
                        catch (final Exception e)
                        {
                            anyException = true;
                            final GatherException ge = new GatherException(
                                "Exception happened in handleException " + docGuid,
                                e,
                                GatherResponse.CODE_FILE_ERROR);
                            publishStatus = "DOC Step Failed Exception happened in fetching doc";

                            if (novusUtility.getShowMissDocsList().equalsIgnoreCase("Y"))
                            {
                                if (novusRetryCounter == 3)
                                {// just write it once after 3 retries
                                    Log.debug(
                                        "Text is not found for the guid "
                                            + document.getGuid()
                                            + " and the error code is "
                                            + document.getErrorCode());
                                    writer.write(document.getGuid());
                                    writer.write("\n");
                                    missingGuidsCount++;
                                }
                            }
                        }
                    }
                }
                missingGuidsCount =
                    createContentFile(document, contentDestinationDirectory, docRetryCount, writer, missingGuidsCount);
                metaDocCounters = createMetadataFile(
                    document,
                    metadataDestinationDirectory,
                    tocSequence,
                    docRetryCount,
                    missingMetaDataCount);

                metaDocFoundCounter += metaDocCounters[META_COUNTER];
                metaDocRetryCounter += metaDocCounters[META_RETRY_COUNTER];

                docFoundCounter++;
            }
        }
        catch (final NovusException e)
        {
            anyException = true;
            final GatherException ge = new GatherException(
                "Novus error occurred fetching document " + docGuid,
                e,
                GatherResponse.CODE_NOVUS_ERROR);
            publishStatus = "DOC Step Failed Novus error occurred fetching document";
            throw ge;
        }
        catch (final IOException e)
        {
            anyException = true;
            final GatherException ge =
                new GatherException("File I/O error writing document " + docGuid, e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "DOC Step Failed File I/O error writing document";

            throw ge;
        }
        catch (final NullPointerException e)
        {
            anyException = true;
            final GatherException ge =
                new GatherException("Null document fetching guid " + docGuid, e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "DOC Step Failed Null document fetching guid";

            throw ge;
        }
        finally
        {
            gatherResponse.setNodeCount(docGuids.size()); // Expected doc count
            gatherResponse.setDocCount(docFoundCounter); // retrieved doc count
            gatherResponse.setRetryCount(novusRetryCounter); // retry doc count
            gatherResponse.setRetryCount2(metaDocRetryCounter); // Meta retry doc count
            gatherResponse.setDocCount2(metaDocFoundCounter); // retrieved doc count
            gatherResponse.setPublishStatus(publishStatus);

/*			if (anyException) {
				// Remove all existing generated document files on any exception
				removeAllFilesInDirectory(contentDestinationDirectory);
				removeAllFilesInDirectory(metadataDestinationDirectory);
			}*/

            novus.shutdownMQ();

            try
            {
                writer.close();
                stream.close();
            }
            catch (final IOException e)
            {
                anyException = true;
                final GatherException ge = new GatherException(
                    "File I/O error writing document " + docGuid,
                    e,
                    GatherResponse.CODE_FILE_ERROR);
                publishStatus = "DOC Step Failed File I/O error writing document";

                throw ge;
            }
            if (missingGuidsCount > 0)
            {
                final GatherException ge = new GatherException(
                    "Null documents are found for the current ebook ",
                    GatherResponse.CODE_FILE_ERROR);
                missingGuidsCount = 0; //reset to 0
                throw ge;
            }
        }
        return (gatherResponse);
    }

    /**
     * @param document
     * @param destinationDirectory
     * @throws NovusException
     * @throws IOException
     */
    private final int createContentFile(
        final Document document,
        final File destinationDirectory,
        final int retryCount,
        final Writer missingsDocs,
        int missingGuidsCount) throws NovusException, IOException
    {
        final String basename = document.getGuid() + EBConstants.XML_FILE_EXTENSION;
        final File destinationFile = new File(destinationDirectory, basename);
        String docText = null;

        // This is the counter for checking how many Novus retries we are making
        // for doc retrieval
        Integer novusDocRetryCounter = 0;
        while (novusDocRetryCounter < retryCount)
        {
            try
            {
                docText = document.getText();
                if ((document.getErrorCode() == null) || (document.getErrorCode().endsWith("00")))
                {
                    break;
                }
                else
                {
                    if (novusUtility.getShowMissDocsList().equalsIgnoreCase("Y"))
                    {
                        if (novusDocRetryCounter == 3)
                        { // just write it once after 3 retries
                            Log.debug(
                                "Text is not found for the guid "
                                    + document.getGuid()
                                    + " and the error code is "
                                    + document.getErrorCode());
                            missingsDocs.write(document.getGuid());
                            missingsDocs.write("\n");
                            missingGuidsCount++;
                        }
                    }

                    Log.error(
                        "Exception happened while retrieving text for the guid "
                            + document.getGuid()
                            + " with an error code "
                            + document.getErrorCode()
                            + "Retry count is "
                            + novusDocRetryCounter);
                    novusDocRetryCounter++;
                }
            }
            catch (final Exception exception)
            {
                try
                {
                    Log.debug("Before calling handleException in createContentFile()");
                    novusDocRetryCounter = novusUtility.handleException(exception, novusDocRetryCounter, retryCount);

                    if (novusUtility.getShowMissDocsList().equalsIgnoreCase("Y"))
                    {
                        if (novusDocRetryCounter == 3)
                        { // just write it once, after 3 retries
                            Log.debug(
                                "Text is not found for the guid "
                                    + document.getGuid()
                                    + " and the error code is "
                                    + document.getErrorCode());
                            missingsDocs.write(document.getGuid());
                            missingsDocs.write("\n");
                            missingGuidsCount++;
                        }
                    }
                }
                catch (final Exception e)
                {
                    Log.error("Exception in handleException() call from createContentFile() " + e.getMessage());
                    novusDocRetryCounter++;
                }
            }
        }
        if (docText != null)
        {
            createFile(docText.toString(), destinationFile);
        }

        return missingGuidsCount;
    }

    /**
     * @param document
     * @param destinationDirectory
     * @param tocSeqNum
     * @throws NovusException
     * @throws IOException
     */
    private final int[] createMetadataFile(
        final Document document,
        final File destinationDirectory,
        final int tocSeqNum,
        final int retryCount,
        int missingMetaDataCount) throws NovusException, IOException
    {
        final String basename =
            tocSeqNum + "-" + document.getCollection() + "-" + document.getGuid() + EBConstants.XML_FILE_EXTENSION;
        final File destinationFile = new File(destinationDirectory, basename);

        String docMetaData = null;
        // This is the counter for checking how many Novus retries we are making
        // for meta data retrieval
        Integer novusMetaRetryCounter = 0;
        Integer novusMetaCounter = 0;
        while (novusMetaRetryCounter < retryCount)
        {
            try
            {
                docMetaData = document.getMetaData();
                novusMetaCounter++;
                if ((document.getErrorCode() == null) || (document.getErrorCode().endsWith("00")))
                {
                    break;
                }
                else
                {
                    Log.error(
                        "Exception happened while retreving metadata for the guid "
                            + document.getGuid()
                            + " with an error code "
                            + document.getErrorCode()
                            + "Retry count is "
                            + novusMetaRetryCounter);
                    novusMetaRetryCounter++;

                    if (novusUtility.getShowMissDocsList().equalsIgnoreCase("Y"))
                    {
                        if (novusMetaRetryCounter == 0)
                        { // just write it once
                            Log.debug(
                                "Metadata is not found for the guid "
                                    + document.getGuid()
                                    + " and the error code is "
                                    + document.getErrorCode());
                            missingMetaDataCount++;
                        }
                    }
                }
            }
            catch (final Exception exception)
            {
                try
                {
                    Log.debug("Before calling handleException in createMetadataFile()");
                    novusMetaRetryCounter = novusUtility.handleException(exception, novusMetaRetryCounter, retryCount);
                }
                catch (final Exception e)
                {
                    Log.error("Exception in handleException createMetadataFile()" + e.getMessage());
                    novusMetaRetryCounter++;
                }
            }
        }
        final int[] counters = {0, 0};
        counters[META_RETRY_COUNTER] = novusMetaRetryCounter;
        counters[META_COUNTER] = novusMetaCounter;
        if (docMetaData != null)
        {
            createFile(docMetaData.toString(), destinationFile);
        }
        return counters;
    }

    /**
     * Create a file containing the specified content.
     */
    private static final void createFile(final String content, final File destinationFile) throws IOException
    {
        final FileOutputStream stream = new FileOutputStream(destinationFile);
        Writer writer = null;
        try
        {
            final String charset = "UTF-8"; // explicitly set the character set
            writer = new OutputStreamWriter(stream, charset);
            writer.write(content);
            writer.flush();
        }
        finally
        {
            writer.close();
            stream.close();
        }
    }

    /**
     * Delete all files in the specified directory.
     * @param directory directory whose files will be removed
     */
    public static void removeAllFilesInDirectory(final File directory)
    {
        FileUtils.deleteQuietly(directory);
    }

    @Required
    public void setNovusFactory(final NovusFactory factory)
    {
        novusFactory = factory;
    }

    @Required
    public void setNovusUtility(final NovusUtility novusUtil)
    {
        novusUtility = novusUtil;
    }
}
