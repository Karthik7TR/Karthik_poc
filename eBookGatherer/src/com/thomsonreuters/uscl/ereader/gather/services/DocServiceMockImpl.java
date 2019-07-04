package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "docServiceMockImpl")
public class DocServiceMockImpl implements DocService {
    private static final String MOCK_COLLECTION_NAME = "w_an_ea_texts2_mock";
    private static final String MOCK_DOCS_DIR = "/eBookGatherer/resources/poc/Novus_Novus_POC/annualPamphlets/mdlitman/Docs";

    @Value("${root.work.directory}")
    private String rootDir;

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
        if (!MOCK_COLLECTION_NAME.equals(collectionName)) {
            return docService.fetchDocuments(
                docGuids,
                collectionName,
                contentDestinationDirectory,
                metadataDestinationDirectory,
                isFinalStage,
                useReloadContent);
        } else {
            try {
                FileUtils.copyDirectory(new File(rootDir, MOCK_DOCS_DIR), contentDestinationDirectory);
            } catch (final IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            final GatherResponse gatherResponse = new GatherResponse();
            gatherResponse.setNodeCount(docGuids.size());
            gatherResponse.setDocCount(docGuids.size());
            gatherResponse.setDocCount2(docGuids.size());
            gatherResponse.setPublishStatus("DOC Gather Step Completed");
            return gatherResponse;
        }
    }

}
