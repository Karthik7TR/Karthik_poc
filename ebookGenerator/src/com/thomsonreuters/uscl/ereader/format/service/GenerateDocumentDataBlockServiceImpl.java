package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import com.thomsonreuters.uscl.ereader.core.service.DateProvider;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class builds Document data block for document file.
 */
@Slf4j
@Service("generateDocumentDataBlockService")
public class GenerateDocumentDataBlockServiceImpl implements GenerateDocumentDataBlockService {
    private static final String DATE_FORMAT = "yyyyMMddhhmmss";
    @Autowired
    private DocMetadataService docMetadataService;
    @Autowired
    private DateProvider dateProvider;

    /**
     * Builds document data block using passed in collection,version information.
     * @param versioned
     * @param collectionName
     *
     */
    private InputStream buildDocumentDataBlock(final String versioned, final String collectionName) {
        final String currentDate = dateString();
        final StringBuffer documentDataBlocks = new StringBuffer();
        documentDataBlocks.append("<document-data>");
        documentDataBlocks.append("<collection>");
        documentDataBlocks.append(collectionName);
        documentDataBlocks.append("</collection>");
        documentDataBlocks.append("<datetime>" + currentDate + "</datetime>");
        documentDataBlocks.append("<versioned>");
        documentDataBlocks.append(versioned);
        documentDataBlocks.append("</versioned>");
        documentDataBlocks.append("<doc-type></doc-type>");
        documentDataBlocks.append("<cite></cite>");
        documentDataBlocks.append("</document-data>");
        return new ByteArrayInputStream(documentDataBlocks.toString().getBytes());
    }

    /**
     * Based on passed in document guid this method retrieve corresponding collectionName using metadata service and builds documentData block.
     * returns as InputStream.
     * @param titleId
     * @param jobInstanceId
     * @param docGuid
     * @return
     * @throws EBookFormatException
     */
    @Override
    public InputStream getDocumentDataBlockAsStream(
        final String titleId,
        final Long jobInstanceId,
        final String docGuid) throws EBookFormatException {
        final DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docGuid);
        if (docMetadata == null) {
            final String message = "Document metadata could not be found for given guid ="
                + docGuid
                + " and title Id ="
                + titleId
                + " and jobInstanceId ="
                + jobInstanceId;
            log.error(message);

            throw new EBookFormatException(message);
        }

        final String collectionName = docMetadata.getCollectionName();
        final String versioned = "False";
        return buildDocumentDataBlock(versioned, collectionName);
    }

    /**
     * Returns data in yyyyMMddhhmmss format.
     * @return
     */
    private String dateString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(dateProvider.getDate());
    }
}
