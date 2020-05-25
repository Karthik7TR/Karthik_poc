package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import lombok.SneakyThrows;
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
    private final String MOCK_INDEX_COLLECTION_NAME = "w_an_ea_texts2_index_toc_mock";
    private final String MOCK_INDEX_TOC_FILE = "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman/Toc/indexDocsSingle.xml";
    private final String MOCK_COLLECTION_NAME_SMALL = "w_an_ea_texts2_toc_mock_small";
    private final String MOCK_TOC_FILE_SMALL = "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman-small/Toc/toc.xml";
    private final String MOCK_INDEX_COLLECTION_NAME_SMALL = "w_an_ea_texts2_index_toc_mock_small";
    private final String MOCK_INDEX_TOC_FILE_SMALL = "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman-small/Toc/indexDocsSingle.xml";
    private final String MOCK_COLLECTION_NAME_GAEVIDENCE = "w_an_ea_texts2_toc_mock_gaevidence";
    private final String MOCK_TOC_FILE_GAEVIDENCE = "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/gaevidence/Toc/toc.xml";
    private final String MOCK_COLLECTION_NAME_THORBURN = "mock_thorburn";
    private final String MOCK_TOC_FILE_THORBURN = "/WEB-INF/poc/Novus_Novus_POC/eLooseleafs/thorburn/Toc/toc.xml";

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
        switch(collectionName) {
        case MOCK_COLLECTION_NAME:
            return copyFile(MOCK_TOC_FILE_SMALL, tocFile);
        case MOCK_INDEX_COLLECTION_NAME:
            return copyFile(MOCK_INDEX_TOC_FILE_SMALL, tocFile);
        case MOCK_COLLECTION_NAME_SMALL:
            return copyFile(MOCK_TOC_FILE_SMALL, tocFile);
        case MOCK_INDEX_COLLECTION_NAME_SMALL:
            return copyFile(MOCK_INDEX_TOC_FILE_SMALL, tocFile);
        case MOCK_COLLECTION_NAME_GAEVIDENCE:
            return copyFile(MOCK_TOC_FILE_GAEVIDENCE, tocFile);
        case MOCK_COLLECTION_NAME_THORBURN:
            return copyFile(MOCK_TOC_FILE_THORBURN, tocFile);
        default:
            return tocService.findTableOfContents(
                guid,
                collectionName,
                tocFile,
                excludeDocuments,
                renameTocEntries,
                isFinalStage,
                splitTocGuidList,
                thresholdValue);
        }
    }

    @SneakyThrows
    private GatherResponse copyFile(final String sourceTocFile, final File targetTocFile) {
        FileUtils.copyFile(new File(servletContext.getRealPath(sourceTocFile)), targetTocFile);

        final GatherResponse gatherResponse = new GatherResponse();
        gatherResponse.setDocCount(DOC_COUNT);
        gatherResponse.setNodeCount(NODE_COUNT);
        gatherResponse.setPublishStatus(PUBLISH_STATUS);
        gatherResponse.setDuplicateTocGuids(Collections.EMPTY_LIST);

        return gatherResponse;
    }

}
