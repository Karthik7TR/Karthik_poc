package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 *
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public interface GenerateDocumentDataBlockService {
    /**
     * Based on passed in document guid this method retrieve corresponding collectionName using metadata service and builds documentData block.
     * returns as InputStream.
     * @param titleId
     * @param jobInstanceId
     * @param docGuid
     * @return
     * @throws EBookFormatException
     */
    InputStream getDocumentDataBlockAsStream(String titleId, Long jobInstanceId, String docGuid)
        throws EBookFormatException;
}
