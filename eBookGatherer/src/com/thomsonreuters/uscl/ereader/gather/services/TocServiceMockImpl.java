package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "tocServiceMockImpl")
public class TocServiceMockImpl implements TocService {
    private static final String PUBLISH_STATUS = "TOC Step Completed";
    private static final int NODE_COUNT = 244;
    private static final int DOC_COUNT = 227;
    private final String MOCK_COLLECTION_NAME = "w_an_ea_texts2_toc_mock";
    private final String MOCK_TOC_FILE = "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman/Toc/toc.xml";

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private TocService tocService;

    @Override
    public GatherResponse findTableOfContents(
        final String guid,
        final String collectionName,
        final File tocFile,
        final List<ExcludeDocument> excludeDocuments,
        final List<RenameTocEntry> renameTocEntries,
        final boolean isFinalStage,
        final List<String> splitTocGuidList,
        final int thresholdValue)
        throws GatherException {
        if (!MOCK_COLLECTION_NAME.contentEquals(collectionName)) {
            return tocService.findTableOfContents(
                guid,
                collectionName,
                tocFile,
                excludeDocuments,
                renameTocEntries,
                isFinalStage,
                splitTocGuidList,
                thresholdValue);
        } else {
            try {
                FileUtils.copyFile(new File(servletContext.getRealPath(MOCK_TOC_FILE)), tocFile);
            } catch (final IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            final GatherResponse gatherResponse = new GatherResponse();
            gatherResponse.setDocCount(DOC_COUNT);
            gatherResponse.setNodeCount(NODE_COUNT);
            gatherResponse.setPublishStatus(PUBLISH_STATUS);
            gatherResponse.setDuplicateTocGuids(Collections.EMPTY_LIST);

            return gatherResponse;
        }
    }

}
