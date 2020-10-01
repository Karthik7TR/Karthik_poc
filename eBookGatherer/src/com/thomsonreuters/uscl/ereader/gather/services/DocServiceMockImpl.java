package com.thomsonreuters.uscl.ereader.gather.services;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component(value = "docServiceMockImpl")
public class DocServiceMockImpl implements DocService {
    @Autowired
    private ServletContext servletContext;

    @Autowired
    private DocService docService;

    @Override
    public GatherResponse fetchDocuments(
        final Collection<String> docGuids,
        final String collectionName,
        final File contentDestinationDirectory,
        final File metadataDestinationDirectory,
        final boolean isFinalStage,
        final boolean useReloadContent)
        throws GatherException {
        return ofNullable(DocCollectionNames.MAP.get(collectionName))
                .map(docsLocation -> copyDocs(docGuids, docsLocation, contentDestinationDirectory))
                .orElseGet(() -> docService.fetchDocuments(
                        docGuids,
                        collectionName,
                        contentDestinationDirectory,
                        metadataDestinationDirectory,
                        isFinalStage,
                        useReloadContent)
                );
    }

    @SneakyThrows
    private GatherResponse copyDocs(final Collection<String> docGuids, final String sourceDir, final File contentDestinationDirectory) {
        FileUtils.copyDirectory(new File(servletContext.getRealPath(sourceDir)), contentDestinationDirectory);

        final GatherResponse gatherResponse = new GatherResponse();
        gatherResponse.setNodeCount(docGuids.size());
        gatherResponse.setDocCount(docGuids.size());
        gatherResponse.setDocCount2(docGuids.size());
        gatherResponse.setPublishStatus("DOC Gather Completed");

        return gatherResponse;
    }

    @Getter
    @AllArgsConstructor
    enum DocCollectionNames {
        MDLITMAN("mock_mdlitman", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman/Docs"),
        MDLITMAN_SMALL("mock_mdlitman_small", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman-small/Docs"),
        GAEVIDENCE("mock_gaevidence", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/gaevidence/Docs"),
        THORBURN("mock_thorburn", "/WEB-INF/poc/Novus_Novus_POC/eLooseleafs/thorburn/Docs"),
        THORBURN_SMALL("mock_thorburn_small", "/WEB-INF/poc/Novus_Novus_POC/eLooseleafs/thorburn-small/Docs"),
        FCF_SMALL("mock_fcf_small", "/WEB-INF/poc/Novus_Novus_POC/mockBooks/fletcherCorporationForms-small/Docs");

        private final String docCollectionName;
        private final String docsLocation;

        public static final Map<String, String> MAP = buildMap();

        private static Map<String, String> buildMap() {
            return Collections.unmodifiableMap(Arrays.stream(DocCollectionNames.values())
                    .collect(Collectors.toMap(DocCollectionNames::getDocCollectionName, DocCollectionNames::getDocsLocation)));
        }
    }
}
