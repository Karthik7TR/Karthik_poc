package com.thomsonreuters.uscl.ereader.gather.services;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import com.thomsonreuters.uscl.ereader.core.book.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.gather.services.DocServiceMockImpl.DocCollectionNames.GAEVIDENCE_INDEX;
import static com.thomsonreuters.uscl.ereader.gather.services.DocServiceMockImpl.DocCollectionNames.GAEVIDENCE_SMALL_INDEX;
import static java.util.Optional.ofNullable;

@Component(value = "docServiceMockImpl")
public class DocServiceMockImpl implements DocService {
    private static final String XML = ".xml";
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
                .map(docsLocation -> copyDocs(docGuids, docsLocation, contentDestinationDirectory, collectionName))
                .map(var -> constructResponse(docGuids))
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
    private boolean copyDocs(final Collection<String> docGuids, final String sourceDir,
                                    final File contentDestinationDirectory, final String collectionName) {
        final File sourceDirectory = new File(servletContext.getRealPath(sourceDir));
        if (Objects.equals(collectionName, GAEVIDENCE_INDEX.getDocCollectionName()) ||
            Objects.equals(collectionName, GAEVIDENCE_SMALL_INDEX.getDocCollectionName())) {
            docGuids.forEach(docGuid ->
                FileUtils.copyFileToDirectory(new File(sourceDirectory, docGuid + XML), contentDestinationDirectory)
            );
        } else {
            FileUtils.copyDirectory(sourceDirectory, contentDestinationDirectory);
        }

        return true;
    }

    @SneakyThrows
    private GatherResponse constructResponse(final Collection<String> docGuids) {
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
        GAEVIDENCE_INDEX("mock_gaevidence_index", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/gaevidence/Index"),
        GAEVIDENCE_SMALL("mock_gaevidence_small", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/gaevidence-small/Docs"),
        GAEVIDENCE_SMALL_INDEX("mock_gaevidence_small_index", "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/gaevidence-small/Index"),
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
