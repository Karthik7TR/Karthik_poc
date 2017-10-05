package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;

/**
 * Implementors of this interface are responsible for marshalling & unmarshalling TitleMetadata.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public interface TitleMetadataService {
    /**
     * Creates a title manifest to be included within the assembled ebook.
     *
     * @param titleManifest the title manifest (title.xml) to create.
     * @param tocXml the TOC structure from which the &lt;toc&gt; &amp; &lt;docs&gt; portions of the manifest are to be derived.
     */
    void generateTitleManifest(
        OutputStream titleManifest,
        InputStream tocXml,
        TitleMetadata titleMetadata,
        Long jobInstanceId,
        File documentsDirectory,
        String altIdDirPath);

    /**
     * Creates a title manifest to be included within the assembled ebook.
     *
     * @param titleManifest the title manifest (title.xml) to create.
     * @param tocXml the TOC structure from which the &lt;toc&gt; &amp; &lt;docs&gt; portions of the manifest are to be derived.
     */
    void generateSplitTitleManifest(
        OutputStream titleManifest,
        InputStream tocXml,
        TitleMetadata titleMetadata,
        Long jobInstanceId,
        File transformedDocsDir,
        String docToSplitBookFile,
        String splitNodeInfoFile);

    void generateTitleXML(
        TitleMetadata titleMetadata,
        List<Doc> docList,
        InputStream splitTitleXMLStream,
        OutputStream titleManifest,
        String altIdDirPath);
}
