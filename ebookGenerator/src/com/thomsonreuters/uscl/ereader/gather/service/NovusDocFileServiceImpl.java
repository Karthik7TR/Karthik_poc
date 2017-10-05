package com.thomsonreuters.uscl.ereader.gather.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.IllegalStateException;

import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter.DocFilenameFilter;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler.NovusDocFileParser;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * Codes Workbench Novus document and metadata split.
 * @author Dong Kim
 */
public class NovusDocFileServiceImpl implements NovusDocFileService {
    private static String CHARSET = "UTF-8"; // explicitly set the character set

    private static final Logger Log = LogManager.getLogger(NovusDocFileServiceImpl.class);

    @Override
    public GatherResponse fetchDocuments(
        final Map<String, Integer> docGuidsMap,
        final File rootCodesWorkbenchLandingStrip,
        final String cwbBookName,
        final List<NortFileLocation> fileLocations,
        final File contentDestinationDirectory,
        final File metadataDestinationDirectory) throws GatherException {
        Assert.isTrue(
            contentDestinationDirectory.exists(),
            "The content destination directory for the documents does not exist: " + contentDestinationDirectory);
        Assert.isTrue(
            contentDestinationDirectory.exists(),
            "The content destination directory for the document metadata does not exist: "
                + metadataDestinationDirectory);

        final GatherResponse gatherResponse = new GatherResponse();
        gatherResponse.setNodeCount(docGuidsMap.size());

        String publishStatus = "DOC Generate Step Completed";
        int missingGuidsCount = 0;
        try (FileOutputStream stream = new FileOutputStream(
            StringUtils.substringBeforeLast(
                contentDestinationDirectory.getAbsolutePath(),
                System.getProperty("file.separator")) + "_doc_missing_guids.txt");
            Writer writer = new OutputStreamWriter(stream, CHARSET)) {
            Integer tocSequence = 0;
            final Map<String, Map<Integer, String>> documentLevelMap = new HashMap<>();
            int nortFileLevel = 1;
            for (final NortFileLocation fileLocation : fileLocations) {
                final String contentPath = String.format("%s/%s", cwbBookName, fileLocation.getLocationName());
                final File contentDirectory = new File(rootCodesWorkbenchLandingStrip, contentPath);
                if (!contentDirectory.exists()) {
                    throw new IllegalStateException(
                        "Expected Codes Workbench content directory does not exist: "
                            + contentDirectory.getAbsolutePath());
                }

                final File[] docFiles = contentDirectory.listFiles(new DocFilenameFilter());
                if (docFiles.length == 0) {
                    throw new IllegalStateException(
                        "Expected Codes Workbench doc file but none exists: " + contentDirectory.getAbsolutePath());
                } else if (docFiles.length > 1) {
                    throw new IllegalStateException(
                        "Too many Codes Workbench doc files exists: " + contentDirectory.getAbsolutePath());
                }
                String docCollectionName = null;
                for (final File docFile : docFiles) {
                    final String fileName = docFile.getName();
                    if (fileName.lastIndexOf("-") > -1) {
                        docCollectionName = fileName.substring(0, fileName.lastIndexOf("-"));
                    } else {
                        throw new IllegalStateException("Codes Workbench doc file does not have DOC collection name.");
                    }

                    final NovusDocFileParser parser = new NovusDocFileParser(
                        docCollectionName,
                        docGuidsMap,
                        contentDestinationDirectory,
                        metadataDestinationDirectory,
                        gatherResponse,
                        tocSequence,
                        nortFileLevel,
                        documentLevelMap);
                    parser.parseXML(docFile);
                    tocSequence = parser.getTocSequence();
                    nortFileLevel++;
                }
            }

            // Process missing documents
            for (final Entry<String, Integer> entry : docGuidsMap.entrySet()) {
                final String docGuid = entry.getKey();
                final int count = entry.getValue();
                if (count > 0) {
                    Log.debug("Document is not found for the guid " + docGuid);
                    writer.write(docGuid);
                    writer.write("\n");
                    missingGuidsCount++;
                }
            }
        } catch (final IOException e) {
            final GatherException ge =
                new GatherException("File I/O error writing missing guid document ", e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "DOC Step Failed File I/O error writing document";

            throw ge;
        } catch (final IllegalStateException e) {
            final GatherException ge = new GatherException(e.getMessage(), e, GatherResponse.CODE_FILE_ERROR);
            publishStatus = "DOC Step Failed Codes Workbench doc file";

            throw ge;
        } catch (final GatherException e) {
            publishStatus = "DOC Step Failed";
            throw e;
        } finally {
            gatherResponse.setPublishStatus(publishStatus);

            if (missingGuidsCount > 0) {
                final GatherException ge = new GatherException(
                    "Null documents are found for the current ebook ",
                    GatherResponse.CODE_FILE_ERROR);
                missingGuidsCount = 0; //reset to 0
                throw ge;
            }
        }
        return (gatherResponse);
    }
}
