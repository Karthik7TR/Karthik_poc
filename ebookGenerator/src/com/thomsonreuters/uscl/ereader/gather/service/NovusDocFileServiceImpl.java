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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Codes Workbench Novus document and metadata split.
 * @author Dong Kim
 */
@Slf4j
public class NovusDocFileServiceImpl implements NovusDocFileService {
    private static String CHARSET = "UTF-8"; // explicitly set the character set

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

        try (FileOutputStream stream = new FileOutputStream(
            StringUtils.substringBeforeLast(
                contentDestinationDirectory.getAbsolutePath(),
                System.getProperty("file.separator")) + "_doc_missing_guids.txt");
            Writer writer = new OutputStreamWriter(stream, CHARSET)) {
            parseDocumentsOnFileLocations(
                docGuidsMap,
                rootCodesWorkbenchLandingStrip,
                cwbBookName,
                fileLocations,
                contentDestinationDirectory,
                metadataDestinationDirectory,
                gatherResponse);

            processMissingDocuments(docGuidsMap, writer);
        } catch (final IOException e) {
            throw new GatherException("DOC Step Failed. File I/O error writing missing guid document ", e, GatherResponse.CODE_FILE_ERROR);
        } catch (final IllegalStateException e) {
            throw new GatherException("DOC Step Failed Codes Workbench doc file. " + e.getMessage(), e, GatherResponse.CODE_FILE_ERROR);
        } catch (final GatherException e) {
            throw e;
        }

        gatherResponse.setPublishStatus("DOC Generate Step Completed");
        return gatherResponse;
    }

    private void parseDocumentsOnFileLocations(
        final Map<String, Integer> docGuidsMap,
        final File rootCodesWorkbenchLandingStrip,
        final String cwbBookName,
        final List<NortFileLocation> fileLocations,
        final File contentDestinationDirectory,
        final File metadataDestinationDirectory,
        final GatherResponse gatherResponse) throws IllegalStateException, GatherException {
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
    }

    private void processMissingDocuments(final Map<String, Integer> docGuidsMap, final Writer writer)
        throws IOException, GatherException {
        int missingGuidsCount = 0;
        for (final Entry<String, Integer> entry : docGuidsMap.entrySet()) {
            final String docGuid = entry.getKey();
            final int count = entry.getValue();
            if (count > 0) {
                log.debug("Document is not found for the guid " + docGuid);
                writer.write(docGuid);
                writer.write("\n");
                missingGuidsCount++;
            }
        }

        if (missingGuidsCount > 0) {
            throw new GatherException("Null documents are found for the current ebook ", GatherResponse.CODE_FILE_ERROR);
        }
    }
}
