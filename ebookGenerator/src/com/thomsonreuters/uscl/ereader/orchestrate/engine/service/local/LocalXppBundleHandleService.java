package com.thomsonreuters.uscl.ereader.orchestrate.engine.service.local;

import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;

public interface LocalXppBundleHandleService {
    /**
     * Creates XppBundleArchive object according to provided material number and
     * bundle file path
     */
    XppBundleArchive createXppBundleArchive(String materialNumber, String srcFile);

    /**
     * Convert provided XppBundleArchive to xml, and send it to jms queue
     * @return jms message
     */
    String sendXppBundleJmsMessage(XppBundleArchive bundleArchive);

    /**
     * Visit to all files in provided directory, and send jms message for each of them
     * @return - count of handled bundle files
     */
    int processXppBundleDirectory(String srcDir);
}
