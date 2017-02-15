package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * @author <a href="mailto:Ravi.Nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 *
 */
public interface KeyCiteBlockGenerationService
{
    /**
     * @param titleId
     * @param jobInstanceId
     * @param docGuid
     * @return
     *
     */
    InputStream getKeyCiteInfo(String titleId, long jobInstanceId, String docGuid) throws EBookFormatException;
}
