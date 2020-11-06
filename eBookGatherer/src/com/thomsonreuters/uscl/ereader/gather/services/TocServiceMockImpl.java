package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component(value = "tocServiceMockImpl")
public class TocServiceMockImpl implements TocService {
    private static final String PUBLISH_STATUS = "TOC Step Completed";
    private static final int NODE_COUNT = 244;
    private static final int DOC_COUNT = 227;

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
        return ofNullable(TocCollectionNames.MAP.get(collectionName))
                .map(tocLocation -> copyFile(tocLocation, tocFile))
                .orElseGet(() -> tocService.findTableOfContents(
                        guid,
                        collectionName,
                        tocFile,
                        excludeDocuments,
                        renameTocEntries,
                        isFinalStage,
                        splitTocGuidList,
                        thresholdValue)
                );
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

    @Getter
    @AllArgsConstructor
    enum TocCollectionNames {
        MDLITMAN("mock_mdlitman", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman/Toc/toc.xml"),
        MDLITMAN_INDEX("mock_mdlitman_index", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman/Toc/indexDocsSingle.xml"),
        MDLITMAN_SMALL("mock_mdlitman_small", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman-small/Toc/toc.xml"),
        MDLITMAN_SMALL_INDEX("mock_mdlitman_small_index", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman-small/Toc/indexDocsSingle.xml"),
        GAEVIDENCE("mock_gaevidence", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/gaevidence/Toc/toc.xml"),
        GAEVIDENCE_SMALL("mock_gaevidence_small", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/gaevidence-small/Toc/toc.xml"),
        THORBURN("mock_thorburn", "/WEB-INF/poc/Novus_Novus_POC/eLooseleafs/thorburn/Toc/toc.xml"),
        THORBURN_SMALL("mock_thorburn_small", "/WEB-INF/poc/Novus_Novus_POC/eLooseleafs/thorburn-small/Toc/toc.xml"),
        FCF_SMALL("mock_fcf_small", "/WEB-INF/poc/Novus_Novus_POC/mockBooks/fletcherCorporationForms-small/Toc/toc.xml");

        private final String tocCollectionName;
        private final String tocLocation;

        public static final Map<String, String> MAP = buildMap();

        private static Map<String, String> buildMap() {
            return Collections.unmodifiableMap(Arrays.stream(TocCollectionNames.values())
                    .collect(Collectors.toMap(TocCollectionNames::getTocCollectionName, TocCollectionNames::getTocLocation)));
        }
    }
}
