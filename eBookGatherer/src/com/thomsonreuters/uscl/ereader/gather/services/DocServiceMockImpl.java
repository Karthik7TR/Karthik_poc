package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.Collection;

import javax.servlet.ServletContext;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "docServiceMockImpl")
public class DocServiceMockImpl implements DocService {
    private static final String MOCK_COLLECTION_NAME = "w_an_ea_texts2_mock";
    private static final String MOCK_DOCS_DIR = "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman/Docs";
    private static final String MOCK_COLLECTION_NAME_SMALL = "w_an_ea_texts2_mock_small";
    private static final String MOCK_DOCS_DIR_SMALL = "/WEB-INF/poc/Novus_Novus_POC/annualPamphlets/mdlitman-small/Docs";

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

        switch(collectionName) {
        case MOCK_COLLECTION_NAME:
            return copyDocs(docGuids, MOCK_DOCS_DIR_SMALL, contentDestinationDirectory);
        case MOCK_COLLECTION_NAME_SMALL:
            return copyDocs(docGuids, MOCK_DOCS_DIR_SMALL, contentDestinationDirectory);
        default:
            return docService.fetchDocuments(
                docGuids,
                collectionName,
                contentDestinationDirectory,
                metadataDestinationDirectory,
                isFinalStage,
                useReloadContent);
        }
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

}
